package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.typed.testkit.StubbedActorContext
import akka.typed.{ActorSystem, ExtensibleBehavior}
import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotStartTime}
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class StateMachineTest extends FunSuite {
  val maxTestTime = Duration(5, TimeUnit.SECONDS)

  test("state machine") {
    // given
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    val currentTimeResult = Instant.parse("2017-12-09T00:05:12.458Z")
    val environment = new EnvironmentStub(currentTimeResult)
    val stateMachine: ExtensibleBehavior[Event] = new StateMachine(environment, AdderState.Initial)
    val actorSystem: ActorSystem[Event] = ActorSystem(stateMachine, "stateMachineActor")
    val actorContext = new StubbedActorContext[Event]("actor-context-stub", mailboxCapacity = 10, actorSystem)

    // when
    stateMachine.receiveMessage(actorContext, Event.ReadyToStart(2))

    // then
    val inboxContents = actorContext.selfInbox.receiveAll()
    assert(environment.currentTimeInvocations === 1)
    assert(inboxContents.size === 3)
    assert(inboxContents.contains(GotStartTime(currentTimeResult)))
    assert(inboxContents.contains(AddedNumber(1)))
    assert(inboxContents.contains(AddedNumber(2)))
  }

  class EnvironmentStub(currentTimeResult: Instant) extends Environment {
    var currentTimeInvocations = 0

    override def emitLine(message: String): Unit = ???

    override def setDone(): Unit = ???

    override def currentTime(): Instant = {
      currentTimeInvocations += 1
      currentTimeResult
    }
  }

}

