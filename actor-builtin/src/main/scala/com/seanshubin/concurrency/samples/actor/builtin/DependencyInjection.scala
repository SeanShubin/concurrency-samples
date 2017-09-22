package com.seanshubin.concurrency.samples.actor.builtin

import java.time.{Clock, Instant}

import akka.typed.{ActorSystem, Behavior}
import com.seanshubin.concurrency.samples.domain._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  val executionContext: ExecutionContext = Implicits.global
  val futureRunner: FutureRunner = new FutureRunnerWithExecutionContext(executionContext)
  val emit: String => Unit = println
  val clock: Clock = Clock.systemUTC()
  val referenceTime: Instant = clock.instant()
  val logger: Logger = new LineEmittingLogger(emit, clock, referenceTime)
  val done: Promise[Unit] = Promise()
  val stateful: Behavior[Event] = new StatefulBehavior(logger.stateChanged, done)
  val eventActorSystem: ActorSystem[Event] = ActorSystem.create(stateful, "state")
  val sendMessage: Event => Unit = eventActorSystem.!
  val worker: Worker = new PrimeNumberWorker(futureRunner, sendMessage)
  val cleanup: () => Unit = eventActorSystem.terminate _
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
