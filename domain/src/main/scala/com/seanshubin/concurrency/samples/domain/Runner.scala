package com.seanshubin.concurrency.samples.domain

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.Duration

class Runner(worker:Worker, done:Awaitable[Unit], cleanup:Cleanup) extends Runnable{
  override def run() = {
    val quantity = 20
    worker.setWorkQuantity(quantity)
    val start = 4000
    val step = 100
    val end = start + quantity * step
    (start until end by step).par.foreach(worker.doWork)
    Await.ready(done, Duration.Inf)
    cleanup.cleanup()
  }
}
