package com.lunatech.mlx.examples.specs2

import org.apache.spark.{SparkConf, SparkContext}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

/**
  * Created by mariadominguez on 06/01/2016.
  */
class SparkJobSpec extends Specification with BeforeAfterAll{
  @transient var sc: SparkContext = _

  override def beforeAll(): Unit = {
    println("Hi")
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("test")
    sc = new SparkContext(conf)
  }

  override def afterAll(): Unit = {
    if (sc != null) {
      sc.stop()
      sc = null
    }
    println("Bye")
  }
}
