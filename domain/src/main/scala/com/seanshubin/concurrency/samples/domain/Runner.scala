package com.seanshubin.concurrency.samples.domain

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

class Runner(worker: Worker, done: Awaitable[Unit], cleanup: () => Unit) extends Runnable {
  override def run(): Unit = {
    val quantity = 20
    worker.setWorkQuantity(quantity)
    val start = 4000
    val step = 100
    val end = start + quantity * step
    (start until end by step).par.foreach(worker.doWork)
    Await.ready(done, Duration.Inf)
    cleanup()
  }
}
