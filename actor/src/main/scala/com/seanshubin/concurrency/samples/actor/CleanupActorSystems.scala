package com.seanshubin.concurrency.samples.actor

import akka.typed.ActorSystem
import com.seanshubin.concurrency.samples.domain.{Cleanup, Event}

class CleanupActorSystems(eventActorSystem: ActorSystem[Event]) extends Cleanup {
  override def cleanup(): Unit = {
    eventActorSystem.terminate()
  }
}
