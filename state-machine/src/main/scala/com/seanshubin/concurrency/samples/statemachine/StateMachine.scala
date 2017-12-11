package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine[EventType](environment: Environment, initialState: State[EventType])
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[EventType] {
  private var state: State[EventType] = initialState

  override def receiveSignal(ctx: ActorContext[EventType], signal: Signal): StateMachine[EventType] = {
    this
  }

  override def receiveMessage(ctx: ActorContext[EventType], event: EventType): StateMachine[EventType] = {
    val (newState, effects) = state.apply(event)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}
