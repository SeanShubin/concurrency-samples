package com.seanshubin.concurrency.samples.actor

import com.seanshubin.concurrency.samples.domain.{Cleanup, Event}

class CleanupActorSystems(eventActorSystem: ActorSystemContract[Event]) extends Cleanup {
  override def cleanup(): Unit = {
    eventActorSystem.terminate()
  }
}
