package com.seanshubin.concurrency.samples.actor.builtin

class CleanupActorSystems(eventActorSystem: ActorSystemContract[Event]) extends Cleanup {
  override def cleanup(): Unit = {
    eventActorSystem.terminate()
  }
}
