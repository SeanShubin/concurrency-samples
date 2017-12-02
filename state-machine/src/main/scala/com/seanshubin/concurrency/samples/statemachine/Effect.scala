package com.seanshubin.concurrency.samples.statemachine

import java.time.{Duration, Instant}

import akka.typed.Signal

sealed trait Effect {
  def apply(environment: Environment, eventListener: Event => Unit): Unit
}

object Effect {

  case object GetStartedTime extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val time = environment.currentTime()
      eventListener(Event.GotStartTime(time))
    }
  }

  case object GetFinishedTime extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val time = environment.currentTime()
      eventListener(Event.GotFinishTime(time))
    }
  }

  case class CreateAddEvents(expectedQuantity: Int) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
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

  case object FinishedComputation extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"finished computation")
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

  case class LogEvent(event: Event) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"event = $event")
    }
  }

  case class GenerateReport(result: Int, startTime: Instant, finishTime: Instant) extends Effect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val duration = Duration.between(startTime, finishTime).toMillis
      val durationString = DurationFormat.MillisecondsFormat.format(duration)
      environment.emitLine(s"result = $result")
      environment.emitLine(durationString)
    }
  }

}