package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine(environment: Environment, initialState: State)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = initialState

  override def receiveSignal(ctx: ActorContext[Event], signal: Signal): StateMachine = {
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], event: Event): StateMachine = {
    val (newState, effects) = state.applyEvent(event)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}
