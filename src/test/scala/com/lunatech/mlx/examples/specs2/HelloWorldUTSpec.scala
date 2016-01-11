package com.lunatech.mlx.examples.specs2

import org.specs2.mutable.Specification

/**
  * UT specification extends org.specs2.mutable.Specification
  */
class HelloWorldUTSpec extends Specification {

  private val hello: String = "Hello world"

  "This is a Unit specification to check the 'Hello world' string" >> {
    "where the 'Hello world' string should contain 11 characters" >> {
      hello must have size(11)
    }
    "where the 'Hello world' string should start with 'Hello'" >> {
      hello must startWith("Hello")
    }
    "where the 'Hello world' string should end with 'world'" >> {
      hello must endWith("world")
    }
  }

  "Adding another specification for numeric values" >> {
    "Example 1 must be true" >> {
      1 must_== 1
      2 must_== 2
    }
    "Example 2 must be true" >> {
      1 must_!= 2
      2 must_!= 1
    }
  }

}
