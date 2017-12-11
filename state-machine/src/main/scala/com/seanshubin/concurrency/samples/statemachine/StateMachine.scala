package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine[EventType, EnvironmentType](environment: EnvironmentType, initialState: State[EventType, EnvironmentType])
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[EventType] {
  private var state: State[EventType, EnvironmentType] = initialState

  override def receiveSignal(ctx: ActorContext[EventType], signal: Signal): StateMachine[EventType, EnvironmentType] = {
    this
  }

  override def receiveMessage(ctx: ActorContext[EventType], event: EventType): StateMachine[EventType, EnvironmentType] = {
    val (newState, effects) = state.apply(event)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}
