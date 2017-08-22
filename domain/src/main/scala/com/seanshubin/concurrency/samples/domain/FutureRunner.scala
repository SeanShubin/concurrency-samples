package com.seanshubin.concurrency.samples.domain

import scala.concurrent.Future

trait FutureRunner {
  def runInFuture[T](f: => T):Future[T]
}
