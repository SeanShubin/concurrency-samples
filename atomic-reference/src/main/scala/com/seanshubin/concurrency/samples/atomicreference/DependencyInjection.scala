package com.seanshubin.concurrency.samples.atomicreference

import java.time.{Clock, Instant}

import com.seanshubin.concurrency.samples.domain._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  val executionContext: ExecutionContext = Implicits.global
  val emit: String => Unit = println
  val clock: Clock = Clock.systemUTC()
  val referenceTime: Instant = clock.instant()
  val logger: Logger = new LineEmittingLogger(emit, clock, referenceTime)
  val unhandledException:Throwable =>Unit = logger.exceptionThrownByFuture
  val futureRunner: FutureRunner = new FutureRunnerWithExecutionContext(executionContext, unhandledException)
  val done: Promise[Unit] = Promise()
  val stateful: Stateful = new StatefulWithAtomicReference(logger.stateChanged, done)
  val worker: Worker = new PrimeNumberWorker(futureRunner, stateful.message)
  val cleanup: () => Unit = () => {}
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
