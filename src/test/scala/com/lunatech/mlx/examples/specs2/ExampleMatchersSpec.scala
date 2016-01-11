package com.lunatech.mlx.examples.specs2

import org.specs2.mutable.Specification

/**
  * Created by mariadominguez on 06/01/2016.
  * Check: https://etorreborre.github.io/specs2/guide/SPECS2-3.7/org.specs2.guide.Matchers.html#out-of-the-box
  */
class ExampleMatchersSpec extends Specification {

  private val helloStr = "Hello world"
  private val helloStr2 = "Hello world"

  "This is my specification" >> {
    s"""EQUALITY - where $helloStr must be equal to $helloStr2""" >> {
      helloStr must be_==(helloStr)
      helloStr must be equalTo(helloStr2)
      helloStr must beEqualTo(helloStr2)
      helloStr must_==(helloStr2)
      helloStr mustEqual(helloStr2)
      helloStr should_==(helloStr2)
      helloStr === helloStr2
    }
  }

}
