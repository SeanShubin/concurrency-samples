package com.seanshubin.concurrency.samples.actor.builtin

class StatefulBehavior(stateChanged: State => Unit, done: Promise[Unit]) extends ExtensibleBehavior[Event] {
  private var state = State.Empty

  override def receiveSignal(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = this

  override def receiveMessage(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    stateChanged(state)
    if (state.isDone) {
      done.success(())
    }
    this
  }
}
