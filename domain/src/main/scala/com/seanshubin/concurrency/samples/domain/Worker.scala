package com.seanshubin.concurrency.samples.domain

import scala.concurrent.Future

trait Worker {
  def setWorkQuantity(quantity: Int):Unit

  def doWork(i: Int): Future[BigInt]
}
