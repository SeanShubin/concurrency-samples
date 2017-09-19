package com.seanshubin.concurrency.samples.actor.builtin

import akka.typed.ActorSystem
import akka.typed.testkit.EffectfulActorContext
import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Promise

class StatefulBehaviorTest extends FunSuite {
  test("work started") {
    withHelper { helper =>
      // given
      val input = 123
      val event = Started("id", input)
      val inProgress = Set[String]("id")
      val started = 1
      val completed = 0
      val expectedQuantity = None
      val expected = State(inProgress, started, completed, expectedQuantity)

      // when
      helper.actorContext.run(event)

      // then
      assert(helper.stateChanged.invocations.size === 1)
      assert(helper.stateChanged.invocations(0) === expected)
      assert(helper.done.isCompleted === false)
    }
  }

  test("work finished") {
    withHelper { helper =>
      // given
      val input = 123
      val output = 456
      val startedEvent = Started("id", input)
      val finishedEvent = Finished("id", input, output)
      val inProgress = Set[String]()
      val started = 1
      val completed = 1
      val expectedQuantity = None
      val expected = State(inProgress, started, completed, expectedQuantity)
      helper.actorContext.run(startedEvent)

      // when
      helper.actorContext.run(finishedEvent)

      // then
      assert(helper.stateChanged.invocations.size === 2)
      assert(helper.stateChanged.invocations(1) === expected)
      assert(helper.done.isCompleted === false)
    }
  }

  test("expect quantity") {
    withHelper { helper =>
      // given
      val quantity = 123
      val event = ExpectQuantity(quantity)
      val inProgress = Set[String]()
      val started = 0
      val completed = 0
      val expectedQuantity = Some(quantity)
      val expected = State(inProgress, started, completed, expectedQuantity)

      // when
      helper.actorContext.run(event)

      // then
      assert(helper.stateChanged.invocations.size === 1)
      assert(helper.stateChanged.invocations(0) === expected)
      assert(helper.done.isCompleted === false)
    }
  }

  test("done") {
    withHelper { helper =>
      // given
      val input = 123
      val output = 456
      val quantity = 1
      val expectQuantityEvent = ExpectQuantity(quantity)
      val startedEvent = Started("id", input)
      val finishedEvent = Finished("id", input, output)

      // when
      helper.actorContext.run(expectQuantityEvent)
      helper.actorContext.run(startedEvent)
      helper.actorContext.run(finishedEvent)

      // then
      assert(helper.done.isCompleted === true)
    }
  }

  class StateChangedStub extends (State => Unit) {
    val invocations: ArrayBuffer[State] = ArrayBuffer()

    override def apply(state: State): Unit = invocations.append(state)
  }

  case class Helper(actorContext: EffectfulActorContext[Event],
                    stateChanged: StateChangedStub,
                    done: Promise[Unit])

  def withHelper(f: Helper => Unit): Unit = {
    // given
    val done = Promise[Unit]()
    val stateChanged = new StateChangedStub
    val statefulBehavior = new StatefulBehavior(stateChanged, done)
    val mailboxCapacity: Int = 1000
    val system: ActorSystem[Event] = ActorSystem.create(statefulBehavior, "behavior")
    val actorContext: EffectfulActorContext[Event] = new EffectfulActorContext[Event](
      "context",
      statefulBehavior,
      mailboxCapacity: Int,
      system)
    val helper = Helper(actorContext, stateChanged, done)
    f(helper)
    system.terminate()
  }
}
