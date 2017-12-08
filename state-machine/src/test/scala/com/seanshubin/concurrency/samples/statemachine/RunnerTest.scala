package com.seanshubin.concurrency.samples.statemachine

import java.util.concurrent.TimeUnit

import akka.typed.Terminated
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Awaitable, CanAwait, Future}

class RunnerTest extends FunSuite {
  test("flow") {
    // given
    val actorSystem = new ActorSystemStub[Event]()
    val done: AwaitableStub[Unit] = new AwaitableStub[Unit]()
    val duration = Duration(5, TimeUnit.SECONDS)
    val runner = new Runner(actorSystem, done, duration)

    // when
    runner.run()

    // then
    assert(actorSystem.messages === Seq(Event.ReadyToStart(10)))
    assert(done.readyInvocations === Seq(duration))
    assert(actorSystem.terminateInvocationCount === 1)
  }

  class AwaitableStub[T] extends Awaitable[T] {
    val readyInvocations = new ArrayBuffer[Duration]()

    override def ready(atMost: Duration)(implicit permit: CanAwait): AwaitableStub.this.type = {
      readyInvocations.append(atMost)
      this
    }

    override def result(atMost: Duration)(implicit permit: CanAwait): T = ???
  }

  class ActorSystemStub[T] extends ActorSystemNotImplemented[T] {
    val messages = new ArrayBuffer[T]
    var terminateInvocationCount = 0

    override def tell(msg: T): Unit = {
      messages.append(msg)
    }

    override def terminate(): Future[Terminated] = {
      terminateInvocationCount += 1
      null
    }
  }

}
