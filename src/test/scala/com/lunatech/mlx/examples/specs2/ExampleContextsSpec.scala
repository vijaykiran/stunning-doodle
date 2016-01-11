package com.lunatech.mlx.examples.specs2

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.specs2.mutable.Specification
import org.specs2.specification.{BeforeAfterEach, BeforeAfterAll}

/**
  * Created by mariadominguez on 06/01/2016.
  */
class ExampleContextsSpec extends SparkJobSpec {
  private val helloStr = "Hello world"
  private val helloStr2 = "Hello world"

  "This is specification 1" >> {
    val input = Seq(1, 2, 3, 4, 5)
    val correctOutput = Array(2, 4, 6, 8, 10)

    "case 1 : return with correct output (custom spark correlation)" >> {
      val inputRDD: RDD[Int] = sc.parallelize(input)
      val output: RDD[Int] = inputRDD.map(_ * 2)
      output.collect() must_== correctOutput
    }
//    "case 2: return with correct output (stats spark correlation)" >> {
//      val inputRDD = sc.parallelize(input)
//      statCorr must_== correctOutput
//    }
//    "case 3: equal to each other" >> {
//      val inputRDD = sc.parallelize(input)
//      statCorr must_== customCorr
//    }
  }


}
