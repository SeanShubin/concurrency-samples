package com.seanshubin.concurrency.samples.actor

import akka.typed.{ActorContext, ActorRef, Behavior, Signal}
import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State}

import scala.concurrent.Promise

class StatefulBehavior(stateChanged:State => Unit, done:Promise[Unit]) extends Behavior[Event] {
  private var state = State.Empty

  override def management(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = this

  override def message(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    stateChanged(state)
    if(state.isDone){
      done.success(())
    }
    this
  }
}
