package com.seanshubin.concurrency.samples.statemachine

trait EventApplier {
  def applyEvent(state: State, event: Event): StateAndEffects
}
