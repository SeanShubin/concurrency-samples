package com.seanshubin.concurrency.samples.locking

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.concurrent.Promise

class StatefulWithLocking(monitor: State => Unit, done: Promise[Unit]) extends Stateful {
  private var state = State.Empty

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => atomicallyTransformState {
        state.startWork(id)
      }
      case Finished(id, _, _) => atomicallyTransformState {
        state.finishWork(id)
      }
      case ExpectQuantity(quantity) => atomicallyTransformState {
        state.expectQuantity(quantity)
      }
    }
  }

  private def atomicallyTransformState(transformState: => State): Unit = {
    synchronized {
      state = transformState
      monitor(state)
      if (state.isDone) {
        done.success(())
      }
    }
  }
}
