package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.Effect.{GetFinishedTime, GetStartedTime}
import com.seanshubin.concurrency.samples.statemachine.Event.{GotFinishTime, GotStartTime}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class EffectTest extends FunSuite {
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

  class EnvironmentWithTimeStub(time: Instant) extends Environment {
    override def emitLine(message: String): Unit = ???

    override def setDone(): Unit = ???

    override def currentTime(): Instant = time
  }

  class EventListenerStub extends (Event => Unit) {
    val events = new ArrayBuffer[Event]

    override def apply(event: Event): Unit = {
      events.append(event)
    }
  }

}
