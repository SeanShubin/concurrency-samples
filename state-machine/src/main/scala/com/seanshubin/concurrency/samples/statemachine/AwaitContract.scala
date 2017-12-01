package com.seanshubin.concurrency.samples.statemachine

import scala.concurrent._
import scala.concurrent.duration.Duration

trait AwaitContract {
  def ready[T](awaitable: Awaitable[T], atMost: Duration): awaitable.type

  def result[T](awaitable: Awaitable[T], atMost: Duration): T
}
