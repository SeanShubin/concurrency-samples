package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine(environment: Environment,
                   eventApplier: EventApplier)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], signal: Signal): StateMachine = {
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], event: Event): StateMachine = {
    val (newState, effects) = eventApplier.applyEvent(state, event)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}
