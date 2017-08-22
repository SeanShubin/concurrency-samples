package com.seanshubin.concurrency.samples.domain

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}

import scala.concurrent.Future

class PrimeNumberWorker(futureRunner: FutureRunner, sendEvent: Event => Unit) extends Worker {
  override def doWork(input: Int): Future[BigInt] = {
    futureRunner.runInFuture {
      val name = s"work $input"
      sendEvent(Started(name, input))
      val output = Prime.nthPrime(input)
      sendEvent(Finished(name, input, output))
      output
    }
  }

  override def setWorkQuantity(quantity: Int): Unit = {
    sendEvent(ExpectQuantity(quantity))
  }
}
