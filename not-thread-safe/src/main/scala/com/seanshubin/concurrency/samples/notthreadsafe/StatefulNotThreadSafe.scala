package com.seanshubin.concurrency.samples.notthreadsafe

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.concurrent.Promise

class StatefulNotThreadSafe(monitor: State => Unit, done: Promise[Unit]) extends Stateful {
  private var state = State.Empty

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    monitor(state)
    if (state.isDone) {
      done.success(())
    }
  }
}
