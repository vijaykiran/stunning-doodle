name := "Specs2Example"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.9"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.5.1"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.5.1"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.5.1"

libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "1.5.1_0.2.1"  % "test"

libraryDependencies += "org.apache.spark" %% "spark-yarn" % "1.5.2" % "test"

//lazy val sscheckVersion = "0.2.0"
//libraryDependencies += "es.ucm.fdi" %% "sscheck" % sscheckVersion
//resolvers += Resolver.bintrayRepo("juanrh", "maven")

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := false

fork in Test := true


assemblyJarName in assembly := s"${name.value}-fat.jar"

// Add exclusions, provided...
assemblyMergeStrategy in assembly := {
  {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}
