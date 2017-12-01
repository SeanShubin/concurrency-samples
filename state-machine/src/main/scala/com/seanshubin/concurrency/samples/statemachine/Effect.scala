package com.seanshubin.concurrency.samples.statemachine

import akka.typed.Signal

sealed trait Effect {
  def apply(environment: Environment, eventListener: Event => Unit): Unit
}

object Effect {

  case class NotifyStarted(expectedQuantity: Int) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val startTime = environment.currentTime()
      eventListener(Event.StartTime(startTime))
      environment.emitLine(s"started with quantity $expectedQuantity")
      for {
        index <- 1 to expectedQuantity
      } {
        eventListener(Event.AddNumber(index))
      }
    }
  }

  case class NotifyAdded(value: Int) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"added $value")
    }
  }

  case object NotifyFinished extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"finished")
    }
  }

  case object ResolveDonePromise extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.setDone()
    }
  }

  case class ReceivedSignal(signal: Signal) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"signal = $signal")
    }
  }

  case class ReceivedEvent(event: Event) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"event = $event")
    }
  }

}