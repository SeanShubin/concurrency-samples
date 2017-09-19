package com.seanshubin.concurrency.samples.notthreadsafe

import com.seanshubin.concurrency.samples.domain._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  val executionContext: ExecutionContext = Implicits.global
  val futureRunner: FutureRunner = new FutureRunnerWithExecutionContext(executionContext)
  val emit: String => Unit = println
  val logger: Logger = new LineEmittingLogger(emit)
  val done: Promise[Unit] = Promise()
  val stateful: Stateful = new StatefulNotThreadSafe(logger.stateChanged, done)
  val worker: Worker = new PrimeNumberWorker(futureRunner, stateful.message)
  val cleanup: () => Unit = () => {}
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
