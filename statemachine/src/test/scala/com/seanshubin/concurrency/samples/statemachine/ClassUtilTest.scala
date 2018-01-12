package com.seanshubin.concurrency.samples.statemachine

import org.scalatest.FunSuite

class ClassUtilTest extends FunSuite {

  case object Bar

  case class Foo(a: Int)

  test("class name") {
    assert(ClassUtil.getSimpleName(classOf[Foo]) === "Foo")
  }

  test("class name of instance") {
    val instance = Foo(123)
    assert(ClassUtil.getSimpleClassName(instance) === "Foo")
  }

  test("class name of object") {
    assert(ClassUtil.getSimpleClassName(Bar) === "Bar")
  }
}
