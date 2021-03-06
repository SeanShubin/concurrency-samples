package com.seanshubin.concurrency.samples.actor

import akka.typed.Terminated
import com.seanshubin.concurrency.samples.domain.Event
import org.scalatest.FunSuite

import scala.concurrent.Future

class CleanupActorSystemsTest extends FunSuite {
  test("cleanup") {
    // given
    val actorSystem = new ActorSystemStub
    val cleanupActorSystems = new CleanupActorSystems(actorSystem)

    // when
    cleanupActorSystems.cleanup()

    // then
    assert(actorSystem.terminateInvocationCount === 1)
  }

  class ActorSystemStub extends ActorSystemNotImplemented[Event] {
    var terminateInvocationCount = 0

    override def terminate(): Future[Terminated] = {
      terminateInvocationCount += 1
      null
    }
  }

}
