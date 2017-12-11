package com.seanshubin.concurrency.samples.statemachine

import java.util.concurrent.TimeUnit

import akka.typed.testkit.StubbedActorContext
import akka.typed.{ActorSystem, ExtensibleBehavior}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class StateMachineTest extends FunSuite {
  val maxTestTime = Duration(5, TimeUnit.SECONDS)
  /* Sample state machine for testing
  state      event      effect                new-state
  is-empty   start      [set-name set-number] is-empty
  is-empty   got-name   [log-name]            has-name
  is-empty   got-number [log-number]          has-number
  has-name   got-number [log-done stop]       done
  has-number got-name   [log-done stop]       done
  */

  test("state machine that is empty gets start") {
    // given
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    val environment = new EnvironmentStub
    val stateMachine: ExtensibleBehavior[EventStub] = new StateMachine(environment, IsEmpty)
    val actorSystem: ActorSystem[EventStub] = ActorSystem(stateMachine, "stateMachineActor")
    val actorContext = new StubbedActorContext[EventStub]("actor-context-stub", mailboxCapacity = 10, actorSystem)

    // when
    stateMachine.receiveMessage(actorContext, Start)

    // then
    val inboxContents = actorContext.selfInbox.receiveAll()

    assert(environment.stopInvocations === 0)
    assert(environment.messages === Seq())
    assert(inboxContents.size === 2)
    assert(inboxContents.contains(GotName("foo")))
    assert(inboxContents.contains(GotNumber(12345)))
  }

  test("state machine that is empty gets name") {
    // given
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    val environment = new EnvironmentStub
    val stateMachine: ExtensibleBehavior[EventStub] = new StateMachine(environment, IsEmpty)
    val actorSystem: ActorSystem[EventStub] = ActorSystem(stateMachine, "stateMachineActor")
    val actorContext = new StubbedActorContext[EventStub]("actor-context-stub", mailboxCapacity = 10, actorSystem)

    // when
    stateMachine.receiveMessage(actorContext, GotName("bar"))

    // then
    val inboxContents = actorContext.selfInbox.receiveAll()

    assert(environment.stopInvocations === 0)
    assert(environment.messages === Seq("got name bar"))
    assert(inboxContents.size === 0)
  }

  test("state machine with name gets number") {
    // given
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    val environment = new EnvironmentStub
    val stateMachine: ExtensibleBehavior[EventStub] = new StateMachine(environment, HasName("foo"))
    val actorSystem: ActorSystem[EventStub] = ActorSystem(stateMachine, "stateMachineActor")
    val actorContext = new StubbedActorContext[EventStub]("actor-context-stub", mailboxCapacity = 10, actorSystem)

    // when
    stateMachine.receiveMessage(actorContext, GotNumber(12345))

    // then
    val inboxContents = actorContext.selfInbox.receiveAll()
    inboxContents.foreach(println)

    assert(environment.stopInvocations === 1)
    assert(environment.messages === Seq("name is foo, number is 12345"))
    assert(inboxContents.size === 0)
  }

  class EnvironmentStub {
    var stopInvocations = 0
    val messages = new ArrayBuffer[String]

    def logMessage(message: String): Unit = messages.append(message)

    def stop(): Unit = stopInvocations += 1
  }

  trait EventStub

  case object Start extends EventStub

  case class GotName(name: String) extends EventStub

  case class GotNumber(number: Int) extends EventStub

  trait EffectStub extends Effect[EventStub, EnvironmentStub]

  case class SetName(name: String) extends EffectStub {
    override def apply(environment: EnvironmentStub, eventListener: EventStub => Unit): Unit = {
      eventListener(GotName("foo"))
    }
  }

  case class SetNumber(number: Int) extends EffectStub {
    override def apply(environment: EnvironmentStub, eventListener: EventStub => Unit): Unit = {
      eventListener(GotNumber(12345))
    }
  }

  case class LogMessage(message: String) extends EffectStub {
    override def apply(environment: EnvironmentStub, eventListener: EventStub => Unit): Unit = {
      environment.logMessage(message)
    }
  }

  case class LogDone(name: String, number: Int) extends EffectStub {
    override def apply(environment: EnvironmentStub, eventListener: EventStub => Unit): Unit = {
      environment.logMessage(s"name is $name, number is $number")
    }
  }

  case object Stop extends EffectStub {
    override def apply(environment: EnvironmentStub, eventListener: EventStub => Unit): Unit = {
      environment.stop()
    }
  }

  trait StateStub extends State[EventStub, EnvironmentStub]

  case object IsEmpty extends StateStub {
    override def apply(event: EventStub): (State[EventStub, EnvironmentStub], Seq[Effect[EventStub, EnvironmentStub]]) = {
      event match {
        case Start =>
          val newState = this
          val effects = Seq(SetName("foo"), SetNumber(12345))
          (newState, effects)
        case GotName(name) =>
          val newState = HasName(name)
          val effects = Seq(LogMessage(s"got name $name"))
          (newState, effects)
        case GotNumber(number) =>
          val newState = HasNumber(number)
          val effects = Seq(LogMessage(s"got number $number"))
          (newState, effects)
      }
    }
  }

  case class HasName(name: String) extends StateStub {
    override def apply(event: EventStub): (State[EventStub, EnvironmentStub], Seq[Effect[EventStub, EnvironmentStub]]) = {
      event match {
        case GotNumber(number) =>
          val newState = Done
          val effects = Seq(LogDone(name, number), Stop)
          (newState, effects)
      }
    }
  }

  case class HasNumber(number: Int) extends StateStub {
    override def apply(event: EventStub): (State[EventStub, EnvironmentStub], Seq[Effect[EventStub, EnvironmentStub]]) = {
      event match {
        case GotName(name) =>
          val newState = Done
          val effects = Seq(LogDone(name, number), Stop)
          (newState, effects)
      }
    }
  }

  case object Done extends StateStub {
    override def apply(event: EventStub): (State[EventStub, EnvironmentStub], Seq[Effect[EventStub, EnvironmentStub]]) = {
      unsupported(event.toString)
    }
  }

}
