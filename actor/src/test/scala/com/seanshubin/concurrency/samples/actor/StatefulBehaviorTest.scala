package com.seanshubin.concurrency.samples.actor

import akka.typed.ActorContext
import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Promise

class StatefulBehaviorTest extends FunSuite {
  test("work started") {
    // given
    val done = Promise[Unit]()
    val stateChanged = new StateChangedStub
    val statefulBehavior = new StatefulBehavior(stateChanged, done)
    val dummyContext: ActorContext[Event] = null
    val input = 123
    val event = Started("id", input)
    val inProgress = Set[String]("id")
    val started = 1
    val completed = 0
    val expectedQuantity = None
    val expected = State(inProgress, started, completed, expectedQuantity)

    // when
    statefulBehavior.message(dummyContext, event)

    // then
    assert(stateChanged.invocations.size === 1)
    assert(stateChanged.invocations(0) === expected)
    assert(done.isCompleted === false)
  }

  test("work finished") {
    // given
    val done = Promise[Unit]()
    val stateChanged = new StateChangedStub
    val statefulBehavior = new StatefulBehavior(stateChanged, done)
    val dummyContext: ActorContext[Event] = null
    val input = 123
    val output = 456
    val startedEvent = Started("id", input)
    val finishedEvent = Finished("id", input, output)
    val inProgress = Set[String]()
    val started = 1
    val completed = 1
    val expectedQuantity = None
    val expected = State(inProgress, started, completed, expectedQuantity)
    statefulBehavior.message(dummyContext, startedEvent)

    // when
    statefulBehavior.message(dummyContext, finishedEvent)

    // then
    assert(stateChanged.invocations.size === 2)
    assert(stateChanged.invocations(1) === expected)
    assert(done.isCompleted === false)
  }

  test("expect quantity") {
    // given
    val done = Promise[Unit]()
    val stateChanged = new StateChangedStub
    val statefulBehavior = new StatefulBehavior(stateChanged, done)
    val dummyContext: ActorContext[Event] = null
    val quantity = 123
    val event = ExpectQuantity(quantity)
    val inProgress = Set[String]()
    val started = 0
    val completed = 0
    val expectedQuantity = Some(quantity)
    val expected = State(inProgress, started, completed, expectedQuantity)

    // when
    statefulBehavior.message(dummyContext, event)

    // then
    assert(stateChanged.invocations.size === 1)
    assert(stateChanged.invocations(0) === expected)
    assert(done.isCompleted === false)
  }

  test("done") {
    // given
    val done = Promise[Unit]()
    val stateChanged = new StateChangedStub
    val statefulBehavior = new StatefulBehavior(stateChanged, done)
    val dummyContext: ActorContext[Event] = null
    val input = 123
    val output = 456
    val quantity = 1
    val expectQuantityEvent = ExpectQuantity(quantity)
    val startedEvent = Started("id", input)
    val finishedEvent = Finished("id", input, output)
    val inProgress = Set[String]()
    val started = 1
    val completed = 1
    val expectedQuantity = None
    val expected = State(inProgress, started, completed, expectedQuantity)

    // when
    statefulBehavior.message(dummyContext, expectQuantityEvent)
    statefulBehavior.message(dummyContext, startedEvent)
    statefulBehavior.message(dummyContext, finishedEvent)

    // then
    assert(done.isCompleted === true)
  }

  class StateChangedStub extends (State => Unit) {
    val invocations = ArrayBuffer[State]()

    override def apply(state: State): Unit = invocations.append(state)
  }

}
