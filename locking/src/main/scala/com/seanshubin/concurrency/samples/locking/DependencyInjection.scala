package com.seanshubin.concurrency.samples.locking

import java.time.{Clock, Instant}

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
  val stateful: Stateful = new StatefulWithLocking(logger.stateChanged, done)
  val worker: Worker = new PrimeNumberWorker(futureRunner, stateful.message)
  val cleanup: () => Unit = () => {}
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
