package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.AdderEffect._
import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotFinishTime, GotStartTime}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class AdderEffectTest extends FunSuite {
  test("get started time") {
    // given
    val time = Instant.parse("2017-12-08T18:13:08.172Z")
    val environment = new EnvironmentWithTimeStub(time)
    val eventListener = new EventListenerStub

    // when
    GetStartedTime.apply(environment, eventListener)

    // then
    assert(eventListener.events === Seq(GotStartTime(time)))
  }

  test("get finished time") {
    // given
    val time = Instant.parse("2017-12-08T18:14:28.304Z")
    val environment = new EnvironmentWithTimeStub(time)
    val eventListener = new EventListenerStub

    // when
    GetFinishedTime.apply(environment, eventListener)

    // then
    assert(eventListener.events === Seq(GotFinishTime(time)))
  }

  test("create add events") {
    // given
    val expectedQuantity = 3
    val environment = new EnvironmentNotImplemented
    val eventListener = new EventListenerStub

    // when
    CreateAddEvents(expectedQuantity).apply(environment, eventListener)

    // then
    assert(eventListener.events === Seq(AddedNumber(1), AddedNumber(2), AddedNumber(3)))
  }

  test("notify added") {
    // given
    val value = 123
    val environment = new EnvironmentCaptureLinesStub
    val eventListener = new EventListenerStub

    // when
    NotifyAdded(value).apply(environment, eventListener)

    // then
    assert(environment.messages === Seq("added 123"))
    assert(eventListener.events === Seq())
  }

  test("resolve done promise") {
    // given
    val environment = new EnvironmentSetDone
    val eventListener = new EventListenerStub

    // when
    ResolveDonePromise.apply(environment, eventListener)

    // then
    assert(environment.setDoneInvocations === 1)
    assert(eventListener.events === Seq())
  }

  test("generate report") {
    // given
    val result = 1000
    val startTime = Instant.parse("2017-12-12T01:44:41.996Z")
    val finishTime = Instant.parse("2017-12-12T01:44:51.691Z")
    val environment = new EnvironmentCaptureLinesStub
    val eventListener = new EventListenerStub
    val effect = GenerateReport(result, startTime, finishTime)

    // when
    effect.apply(environment, eventListener)

    // then
    assert(environment.messages === Seq("result = 1000", "9 seconds 695 milliseconds"))
    assert(eventListener.events === Seq())
  }

  class EnvironmentNotImplemented extends Environment {
    override def emitLine(message: String): Unit = ???

    override def setDone(): Unit = ???

    override def currentTime(): Instant = ???
  }

  class EnvironmentWithTimeStub(time: Instant) extends EnvironmentNotImplemented {
    override def currentTime(): Instant = time
  }

  class EventListenerStub extends (Event => Unit) {
    val events = new ArrayBuffer[Event]

    override def apply(event: Event): Unit = {
      events.append(event)
    }
  }

  class EnvironmentCaptureLinesStub extends EnvironmentNotImplemented {
    val messages = new ArrayBuffer[String]

    override def emitLine(message: String): Unit = {
      messages.append(message)
    }
  }

  class EnvironmentSetDone extends EnvironmentNotImplemented {
    var setDoneInvocations = 0

    override def setDone(): Unit = setDoneInvocations += 1
  }
}
