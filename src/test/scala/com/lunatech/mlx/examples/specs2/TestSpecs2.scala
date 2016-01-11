package com.lunatech.mlx.examples.specs2

import java.io.File

//import com.holdenkarau.spark.testing.{SharedMiniCluster, SharedSparkContext}
import org.apache.hadoop.fs.FileUtil
import org.apache.spark.rdd.RDD
//import org.scalatest.FunSuite
import org.specs2.matcher.FileMatchers
import org.specs2.mutable.Specification


/**
  * Created by mariadominguez on 08/01/2016.
  */
//class TestSpecs2 extends FunSuite with SharedSparkContext {
//
//  test("Example test") {
//    val input: Seq[Int] = Seq(1, 2, 3, 4, 5)
//    val validOutput: Array[Int] = Array(2, 4, 6, 8, 10)
//
//      val inputRDD: RDD[Int] = sc.parallelize(input)
//      val output: RDD[Int] = inputRDD.map(_ * 2)
//
//      assert(output.collect() sameElements validOutput)
//  }
//
//  test("File-related tests") {
//    val path: String = "/tmp/KDD.Data.99/kddcup.data_10_percent"
//
//    assert(sc.isLocal)
//  }
//
//}

class TestSpecs2 extends SparkJobSpec with FileMatchers {

  "Example test" >> {
    val input: Seq[Int] = Seq(1, 2, 3, 4, 5)
    val validOutput: Array[Int] = Array(2, 4, 6, 8, 10)

    "Applying operation to input, then output is correct" >> {
      val inputRDD: RDD[Int] = sc.parallelize(input)
      val output: RDD[Int] = inputRDD.map(_ * 2)

      output.collect() must_== validOutput
    }
  }

  "File-related tests" >> {
    val path: String = "/tmp/KDD.Data.99/kddcup.data_10_percent"

    "file exists" >> {
      new File(path) must exist
    }

    "file is readable" >> {
      new File(path) must beReadable
    }
  }

}

