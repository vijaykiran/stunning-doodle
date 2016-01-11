package com.lunatech.mlx.examples.specs2

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by mariadominguez on 06/01/2016.
  */
object Example {

  def main(args: Array[String]): Unit = {

    val PATH = "/tmp/KDD.Data.99/kddcup.data_10_percent"

    // SparkContext configuration
    val conf: SparkConf = new SparkConf().setAppName("Kdd Cup").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)

    // Load and parse Data
    val rawData: RDD[String] = sc.textFile(PATH)
    val tmpData: RDD[Array[String]] = rawData.map(_.split(","))
  }


}
