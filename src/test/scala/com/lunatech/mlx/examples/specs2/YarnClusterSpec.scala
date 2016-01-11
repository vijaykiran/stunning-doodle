package com.lunatech.mlx.examples.specs2

import java.io.{OutputStreamWriter, FileOutputStream, IOException, File}
import java.net.URLClassLoader
import java.util.{Properties, UUID}
import java.util.concurrent.TimeUnit

import com.google.common.base.Charsets._
import com.google.common.io.Files
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.hdfs.{HdfsConfiguration, MiniDFSCluster}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.server.MiniYARNCluster
import org.apache.spark.{SparkConf, SparkContext}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.collection.JavaConversions._

/**
  * Created by mariadominguez on 07/01/2016.
  */
class YarnClusterSpec extends Specification with BeforeAfterAll {

  private val shutdownDeletePaths = new scala.collection.mutable.HashSet[String]()

  // log4j configuration for the YARN containers, so that their output is collected
  // by YARN instead of trying to overwrite unit-tests.log.
  private val LOG4J_CONF = """
     |log4j.rootCategory=DEBUG, console
     |log4j.appender.console=org.apache.log4j.ConsoleAppender
     |log4j.appender.console.target=System.err
     |log4j.appender.console.layout=org.apache.log4j.PatternLayout
     |log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n
                           """.stripMargin


  private val configurationFilePath = new File(
    this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath)
    .getParentFile.getAbsolutePath + "/hadoop-site.xml"

  println("0****************" + configurationFilePath)

  @transient private var _sc: SparkContext = _
  @transient private var yarnCluster: MiniYARNCluster = null
  var miniDFSCluster: MiniDFSCluster = null
  private var tempDir: File = _
  private var logConfDir: File = _

  def sc: SparkContext = _sc
  val master = "yarn-client"

  override def beforeAll() {
    // Try and do setup, and in-case we fail shutdown
    try {
      println("setup")
      setup()
    } catch {
      case e: Exception => {
        println("shutdown")
        shutdown()
        throw e
      }
    }
  }

  override def afterAll() {
    println("shutdown")
    shutdown()
  }

  // Program specific class path, override if this isn't working for you
  // TODO: This is a hack, but classPathFromCurrentClassLoader isn't sufficient :(
  def extraClassPath(): Seq[String] = {
    List(
      // Likely sbt classes & test-classes directory
      new File("target/scala-2.10/classes"),
      new File("target/scala-2.10/test-classes"),
      // Likely maven classes & test-classes directory
      new File("target/classes"),
      new File("target/test-classes")
    ).map(_.getAbsolutePath).filter(_ != null)
  }

  // Class path based on current env + program specific class path.
  def classPathFromCurrentClassLoader(): Seq[String] = {
    // This _assumes_ that either the current class loader or parent class loader is a
    // URLClassLoader
    val urlClassLoader = Thread.currentThread().getContextClassLoader match {
      case uc: URLClassLoader => uc
      case xy => xy.getParent.asInstanceOf[URLClassLoader]
    }
    urlClassLoader.getURLs.toSeq.map(u => new File(u.toURI).getAbsolutePath)
  }

  def generateClassPath(): String = {
    // Class path
    val clList = List(logConfDir.getAbsolutePath, sys.props("java.class.path")) ++
      classPathFromCurrentClassLoader() ++ extraClassPath()
    val clPath = clList.mkString(File.pathSeparator)
    clPath
  }

  def shutdown() {
    if (yarnCluster != null) {
      yarnCluster.stop()
    }
    if (_sc != null) {
      _sc.stop()
    }
    System.clearProperty("SPARK_YARN_MODE")
    _sc = null
    yarnCluster = null
  }

  def setup() {
    tempDir = createTempDir()
    println("1****************" + tempDir)
    logConfDir = new File(tempDir, "log4j")
    println("2****************" + logConfDir)
    logConfDir.mkdir()
    System.setProperty("SPARK_YARN_MODE", "true")

    val logConfFile = new File(logConfDir, "log4j.properties")
    Files.write(LOG4J_CONF, logConfFile, UTF_8)

    val yarnConf = new YarnConfiguration()
    yarnCluster = new MiniYARNCluster(getClass.getName, 1, 1, 1)
    yarnCluster.init(yarnConf)
    yarnCluster.start()
    val config = yarnCluster.getConfig
    val deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10)
    while (config.get(YarnConfiguration.RM_ADDRESS).split(":")(1) == "0") {
      if (System.currentTimeMillis() > deadline) {
        throw new IllegalStateException("Timed out waiting for RM to come up.")
      }
      TimeUnit.MILLISECONDS.sleep(100)
    }

    // Find the spark assembly jar
    // TODO: Better error messaging
    val sparkAssemblyDir = sys.env("SPARK_HOME") + "/assembly/target/scala-2.10/"
//    val sparkAssemblyDir = "/Users/mariadominguez/work/ing/Examples/Specs2Example/target/scala-2.10/"
    println("3****************" + sparkAssemblyDir)
    val sparkLibDir = sys.env("SPARK_HOME") + "/lib/"
    println("4****************" + sparkLibDir)
    val candidates = List(new File(sparkAssemblyDir).listFiles,
      new File(sparkLibDir).listFiles).filter(_ != null).flatMap(_.toSeq)

    val sparkAssemblyJar = candidates.filter{f =>
      val name = f.getName
      name.endsWith(".jar") && name.startsWith("spark-assembly")}
      .headOption.getOrElse(throw new Exception(
      "Failed to find spark assembly jar, make sure SPARK_HOME is set correctly"))
      .getAbsolutePath

//    val sparkAssemblyJar = candidates.filter{f =>
//      val name = f.getName
//      name.endsWith(".jar") && name.startsWith("Specs2Example-fat")}
//      .headOption.getOrElse(throw new Exception(
//      "Failed to find spark assembly jar, make sure SPARK_HOME is set correctly"))
//      .getAbsolutePath
    println("5****************" + sparkAssemblyJar)

    // Set some yarn props
    sys.props += ("spark.yarn.jar" -> ("local:" + sparkAssemblyJar))
    sys.props += ("spark.executor.instances" -> "1")
    // Figure out our class path
    val childClasspath = generateClassPath()
    println("6****************" + childClasspath)
    sys.props += ("spark.driver.extraClassPath" -> childClasspath)
    sys.props += ("spark.executor.extraClassPath" -> childClasspath)
    val configurationFile = new File(configurationFilePath)
    if (configurationFile.exists()) {
      configurationFile.delete()
    }
    val configuration = yarnCluster.getConfig
    iterableAsScalaIterable(configuration).foreach { e =>
      println("10************************" + "spark.hadoop." + e.getKey -> e.getValue)
      sys.props += ("spark.hadoop." + e.getKey -> e.getValue)
    }
    configuration.writeXml(new FileOutputStream(configurationFile))
    // Copy the system props
    val props = new Properties()
    sys.props.foreach { case (k, v) =>
      if (k.startsWith("spark.")) {
        props.setProperty(k, v)
      }
    }
    val propsFile = File.createTempFile("spark", ".properties", tempDir)
    val writer = new OutputStreamWriter(new FileOutputStream(propsFile), UTF_8)
    props.store(writer, "Spark properties.")
    writer.close()

    // Set up DFS
    val hdfsConf = new HdfsConfiguration(yarnConf)
    miniDFSCluster = new MiniDFSCluster.Builder(hdfsConf)
      .nameNodePort(9020).format(true).build()
    miniDFSCluster.waitClusterUp()

    val r = miniDFSCluster.getConfiguration(0)
    miniDFSCluster.getFileSystem.mkdir(new Path("/tmp"), new FsPermission(777.toShort))

    val sparkConf = new SparkConf().setMaster(master).setAppName("test")
    _sc = new SparkContext(sparkConf)
  }



  //-----------------------------------------

  /**
    * Create a directory inside the given parent directory. The directory is guaranteed to be
    * newly created, and is not marked for automatic deletion.
    */
  def createDirectory(root: String): File = {
    var attempts = 0
    val maxAttempts = 10
    var dir: File = null
    while (dir == null) {
      attempts += 1
      if (attempts > maxAttempts) {
        throw new IOException("Failed to create a temp directory (under " + root + ") after " +
          maxAttempts + " attempts!")
      }
      try {
        dir = new File(root, "spark-" + UUID.randomUUID.toString)
        if (dir.exists() || !dir.mkdirs()) {
          dir = null
        }
      } catch { case e: SecurityException => dir = null; }
    }

    dir
  }

  /**
    * Create a temporary directory inside the given parent directory. The directory will be
    * automatically deleted when the VM shuts down.
    */
  def createTempDir(root: String = System.getProperty("java.io.tmpdir")): File = {
    val dir = createDirectory(root)
    registerShutdownDeleteDir(dir)
    dir
  }

  // Register the path to be deleted via shutdown hook
  def registerShutdownDeleteDir(file: File) {
    val absolutePath = file.getAbsolutePath
    shutdownDeletePaths.synchronized {
      shutdownDeletePaths += absolutePath
    }
  }

}
