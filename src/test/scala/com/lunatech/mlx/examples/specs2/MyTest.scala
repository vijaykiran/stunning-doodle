package com.lunatech.mlx.examples.specs2

import java.io.File

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.{HdfsConfiguration, MiniDFSCluster}
import org.apache.hadoop.yarn.server.MiniYARNCluster

import org.specs2.mutable.Specification


/**
  * Created by mariadominguez on 08/01/2016.
  */
class MyTest extends Specification {//} with SharedSparkContext{

  private val CLUSTER_1 = "myCluster"
  private var testDataPath: File = _
  private var conf: HdfsConfiguration = _
  private var cluster: MiniDFSCluster = _

  def setup(): Unit = {
    testDataPath = new File(this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath)
    System.clearProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA)
    conf = new HdfsConfiguration()
  }
}
