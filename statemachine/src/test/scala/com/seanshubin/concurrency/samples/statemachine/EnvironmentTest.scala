package com.seanshubin.concurrency.samples.statemachine

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.charset.{Charset, StandardCharsets}
import java.time.{Clock, Instant, ZoneId}

import org.scalatest.FunSuite

import scala.concurrent.Promise

class EnvironmentTest extends FunSuite {
  val charset: Charset = StandardCharsets.UTF_8
  val dummyPromise: Promise[Unit] = null
  val dummyClock: Clock = null
  val dummySystem: SystemContract = null

  test("emit line") {
    // given
    val systemStub = new SystemStub
    val environment = new EnvironmentImpl(
      dummyPromise, dummyClock, systemStub)
    // when
    environment.emitLine("the line")
    // then
    assert(systemStub.text === "the line\n")
  }

  test("set done") {
    // given
    val done = Promise[Unit]()
    val environment = new EnvironmentImpl(
      done, dummyClock, dummySystem)
    // when
    environment.setDone()
    // then
    assert(done.isCompleted === true)
  }

  test("current time") {
    // given
    val expectedTime = Instant.parse("2017-12-04T18:42:28.292Z")
    val clockStub = new ClockStub(expectedTime)
    val environment = new EnvironmentImpl(
      dummyPromise, clockStub, dummySystem)
    // when
    val currentTime = environment.currentTime()
    // then
    assert(currentTime === expectedTime)
  }

  class SystemStub extends SystemNotImplemented {
    def text: String = IoUtil.bytesToString(byteArrayOutputStream.toByteArray, charset)

    val byteArrayOutputStream = new ByteArrayOutputStream()

    override def out: PrintStream = new PrintStream(byteArrayOutputStream)
  }

  class ClockStub(theInstant: Instant) extends Clock {
    override def withZone(zone: ZoneId): Clock = ???

    override def getZone: ZoneId = ???

    override def instant(): Instant = theInstant
  }

}
