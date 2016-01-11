package com.lunatech.mlx.examples.specs2

import org.specs2.Specification
import org.specs2.matcher.ThrownExpectations

/**
  * Acceptance specification extends org.specs2.Specification
  * If you want to declare several expectations per example,
  *   you can mix-in the org.specs2.execute.ThrownExpectations trait
  *   to the specification.
  */
class HelloWorldSpec extends Specification with ThrownExpectations {

  private val hello: String = "Hello world"
  def is = s2"""
    This is an Acceptance specification to check the 'Hello world' string

    The 'Hello world' string should
      contain 11 characters         $e1
      start with 'Hello'            $e2
      end with 'world'              $e3

    This is another case for numeric values:
      Example 4 must be true        $e4
      Example 5 must be true        $e5
    """

  def e1 = hello must have size 11
  def e2 = hello must startWith("Hello")
  def e3 = hello must endWith("world")
  def e4 = (1 must_== 1) and (2 must_== 2)
  def e5 = (1 must_!= 2) and (2 must_!= 1)

}
