package com.seanshubin.concurrency.samples.domain

import scala.concurrent.{ExecutionContext, Future}

class FutureRunnerWithExecutionContext(executionContext: ExecutionContext,
                                       unhandledException: Throwable => Unit) extends FutureRunner {
  private implicit val implicitExecutionContext = executionContext

  override def runInFuture[T](f: => T): Future[T] = {
    Future {
      try {
        f
      } catch {
        case ex:Throwable =>
          unhandledException(ex)
          throw ex
      }
    }
  }
}
