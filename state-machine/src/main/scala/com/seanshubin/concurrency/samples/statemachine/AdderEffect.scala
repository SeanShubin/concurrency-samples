package com.seanshubin.concurrency.samples.statemachine

import java.time.{Duration, Instant}

sealed trait AdderEffect extends Effect[Event, Environment] {
  def apply(environment: Environment, eventListener: Event => Unit): Unit
}

object AdderEffect {
  case object GetStartedTime extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val time = environment.currentTime()
      eventListener(Event.GotStartTime(time))
    }
  }

  case object GetFinishedTime extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val time = environment.currentTime()
      eventListener(Event.GotFinishTime(time))
    }
  }

  case class CreateAddEvents(expectedQuantity: Int) extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      (1 to expectedQuantity).par.foreach(x => eventListener(Event.AddedNumber(x)))
    }
  }

  case class NotifyAdded(value: Int) extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.emitLine(s"added $value")
    }
  }

  case object ResolveDonePromise extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      environment.setDone()
    }
  }

  case class GenerateReport(result: Int, startTime: Instant, finishTime: Instant) extends AdderEffect {
    override def apply(environment: Environment, eventListener: Event => Unit): Unit = {
      val duration = Duration.between(startTime, finishTime).toMillis
      val durationString = DurationFormat.MillisecondsFormat.format(duration)
      environment.emitLine(s"result = $result")
      environment.emitLine(durationString)
    }
  }

}