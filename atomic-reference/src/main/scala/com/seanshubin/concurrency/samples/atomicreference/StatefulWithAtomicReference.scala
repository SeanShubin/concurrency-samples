package com.seanshubin.concurrency.samples.atomicreference

import java.util.concurrent.atomic.AtomicReference

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.annotation.tailrec
import scala.concurrent.Promise

class StatefulWithAtomicReference(monitor: State => Unit, done: Promise[Unit]) extends Stateful {
  private val state = new AtomicReference[State](State.Empty)

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => atomicallyTransformState {
        state.get.startWork(id)
      }
      case Finished(id, _, _) => atomicallyTransformState {
        state.get.finishWork(id)
      }
      case ExpectQuantity(quantity) => atomicallyTransformState {
        state.get.expectQuantity(quantity)
      }
    }
  }

  @tailrec
  private def atomicallyTransformState(transformState: => State): Unit = {
    val oldState = state.get()
    val newState = transformState
    if (state.compareAndSet(oldState, newState)) {
      monitor(newState)
      if (state.get.isDone) {
        done.success(())
      }
    } else {
      atomicallyTransformState(transformState)
    }
  }
}
