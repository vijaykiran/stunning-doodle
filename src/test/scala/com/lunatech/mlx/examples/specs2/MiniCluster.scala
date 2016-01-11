package com.lunatech.mlx.examples.specs2

import com.holdenkarau.spark.testing._
import org.scalatest.FunSuite

class MiniCluster extends FunSuite /*with SharedSparkContext {//} */with SharedMiniCluster {

  val input = List(("Fred", 80.0), ("Fred", 90), ("Fred", 90.0), ("Wilma", 100.0), ("Wilma", 100))

  test("countByKey test") {
    val peopleScores = sc.parallelize(input)
    val scores = peopleScores.countByKey().toList
    val expected = List(("Fred", 3), ("Wilma", 2))
    assert(scores.contains(expected.head))
    assert(scores.contains(expected(1)))
    assert(scores.size === 2)
  }

}
