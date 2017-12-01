package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine(environment: Environment,
                   eventApplier: EventApplier)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], signal: Signal): StateMachine = {
    applyEffect(Effect.ReceivedSignal(signal), ctx)
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], event: Event): StateMachine = {
    applyEffect(Effect.LogEvent(event), ctx)
    val StateAndEffects(newState, effects) = eventApplier.applyEvent(state, event)
    applyEffect(Effect.LogStateTransition(state, newState), ctx)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }

  private def applyEffect(effect: Effect, ctx: ActorContext[Event]): Unit = {
    effect.apply(environment, ctx.asScala.self.tell)
  }
}
