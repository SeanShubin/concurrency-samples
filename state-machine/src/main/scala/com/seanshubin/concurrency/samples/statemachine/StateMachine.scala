package com.seanshubin.concurrency.samples.statemachine

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}
import com.seanshubin.concurrency.samples.statemachine.Event.{AddNumber, GotFinishTime, GotStartTime, Start}

import scala.concurrent.ExecutionContext

class StateMachine(environment: Environment)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], signal: Signal): StateMachine = {
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], event: Event): StateMachine = {
    val (newState, effects) = event match {
      case Start(expectedQuantity) => state.readyToGetStarted(expectedQuantity)
      case AddNumber(value) => state.numberAdded(value)
      case GotStartTime(value) => state.startTimeChecked(value)
      case GotFinishTime(value) => state.endTimeChecked(value)
    }
    state = newState
    effects.foreach(_.apply(environment, ctx.asScala.self.tell))
    this
  }
}
