package com.seanshubin.concurrency.samples.domain

import scala.concurrent.{ExecutionContext, Future}

class FutureRunnerWithExecutionContext(executionContext: ExecutionContext) extends FutureRunner {
  private implicit val implicitExecutionContext = executionContext

  override def runInFuture[T](f: => T): Future[T] = {
    Future {
      f
    }
  }
}
