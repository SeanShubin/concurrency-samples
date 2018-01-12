package com.seanshubin.concurrency.samples.statemachine

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

class Runner(actorSystem: ActorSystemContract[Event],
             done: Awaitable[Unit],
             duration: Duration) extends Runnable {
  override def run(): Unit = {
    actorSystem.tell(Event.ReadyToStart(10))
    try {
      Await.ready(done, duration)
    } finally {
      actorSystem.terminate()
    }
  }
}
