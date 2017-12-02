package com.seanshubin.concurrency.samples.statemachine

import com.seanshubin.concurrency.samples.statemachine.Event.{AddNumber, GotFinishTime, GotStartTime, Start}

class EventApplierImpl extends EventApplier {
  def applyEvent(state: State, event: Event): (State, Seq[Effect]) = {
    event match {
      case Start(expectedQuantity) => state.readyToGetStarted(expectedQuantity)
      case AddNumber(value) => state.numberAdded(value)
      case GotStartTime(value) => state.startTimeChecked(value)
      case GotFinishTime(value) => state.endTimeChecked(value)
    }
  }
}
