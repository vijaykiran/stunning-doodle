package com.lunatech.mlx.examples.specs2

/**
  * Created by mariadominguez on 07/01/2016.
  */
class TestForYarn extends YarnClusterSpec {

  private val helloStr = "Hello world"

  "First test. Yarn is running" >> {
    helloStr must_== helloStr
  }
}
