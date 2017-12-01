package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine(environment: Environment,
                   eventApplier: EventApplier)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], signal: Signal): StateMachine = {
    Effect.ReceivedSignal(signal).apply(environment, ctx.asScala.self.tell)
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], event: Event): StateMachine = {
    Effect.ReceivedEvent(event).apply(environment, ctx.asScala.self.tell)
    val StateAndEffects(newState, effects) = eventApplier.applyEvent(state, event)
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}

/*
state/event/effect

initial
    get started
        get start time
        create add events
        create start time event
processing
    add event
        create finished computation event
        get end time
        create end time event
    start time event
finished computation
    end time
        generate report
*/
