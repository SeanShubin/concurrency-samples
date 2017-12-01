package com.seanshubin.concurrency.samples.statemachine

import akka.typed.ActorSystem

import scala.concurrent.Future
import scala.concurrent.duration.Duration

class Runner(await: AwaitContract,
             actorSystem: ActorSystem[Event],
             done: Future[Unit],
             duration: Duration) extends Runnable {
  override def run(): Unit = {
    actorSystem ! Event.Start(10)
    try {
      await.ready(done, duration)
    } finally {
      actorSystem.terminate()
    }
  }
}
