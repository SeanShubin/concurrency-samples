package com.seanshubin.concurrency.samples.atomicreference

import com.seanshubin.concurrency.samples.domain._

import scala.concurrent.{ExecutionContext, Promise}
import scala.concurrent.ExecutionContext.Implicits

trait DependencyInjection {
  val executionContext: ExecutionContext = Implicits.global
  val futureRunner: FutureRunner = new FutureRunnerWithExecutionContext(executionContext)
  val emit: String => Unit = println
  val logger: Logger = new LineEmittingLogger(emit)
  val done:Promise[Unit] = Promise()
  val stateful: Stateful = new StatefulWithAtomicReference(logger.stateChanged, done)
  val worker: Worker = new PrimeNumberWorker(futureRunner, stateful.message)
  val cleanup:Cleanup = new NoCleanupNeeded
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
