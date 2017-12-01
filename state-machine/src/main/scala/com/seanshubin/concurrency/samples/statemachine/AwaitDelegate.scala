package com.seanshubin.concurrency.samples.statemachine

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

object AwaitDelegate extends AwaitContract {
  override def ready[T](awaitable: Awaitable[T], atMost: Duration): awaitable.type = {
    Await.ready(awaitable, atMost)
  }

  override def result[T](awaitable: Awaitable[T], atMost: Duration): T = {
    Await.result(awaitable, atMost)
  }
}
