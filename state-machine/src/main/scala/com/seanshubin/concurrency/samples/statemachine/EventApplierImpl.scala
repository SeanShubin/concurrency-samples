package com.seanshubin.concurrency.samples.statemachine

import com.seanshubin.concurrency.samples.statemachine.Event.{AddNumber, FinishTime, Start, StartTime}

class EventApplierImpl extends EventApplier {
  def applyEvent(state: State, event: Event): StateAndEffects = {
    event match {
      case Start(expectedQuantity) => state.start(expectedQuantity)
      case AddNumber(value) => state.addNumber(value)
      case StartTime(value) => state.startTime(value)
      case FinishTime(value) => state.finishTime(value)
    }
  }
}
