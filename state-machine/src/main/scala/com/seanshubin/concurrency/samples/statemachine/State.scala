package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.Event.{AddNumber, GotFinishTime, GotStartTime, Start}

/*
state/event/effect

initial
    ready to get started
        get start time
        create add events
        create start time event
processing
    number added
        create finished computation event
        get end time
        create end time event
    start time checked
finished computation
    end time checked
        generate report
*/

sealed trait State {
  def applyEvent(event: Event): (State, Seq[Effect]) = {
    event match {
      case Start(expectedQuantity) => readyToGetStarted(expectedQuantity)
      case AddNumber(value) => numberAdded(value)
      case GotStartTime(value) => startTimeChecked(value)
      case GotFinishTime(value) => endTimeChecked(value)
    }
  }

  def readyToGetStarted(expectedQuantity: Int): (State, Seq[Effect]) = {
    unsupported(s"start($expectedQuantity)")
  }

  def numberAdded(value: Int): (State, Seq[Effect]) = {
    unsupported(s"addNumber($value)")
  }

  def startTimeChecked(value: Instant): (State, Seq[Effect]) = {
    unsupported(s"startTime($value)")
  }

  def endTimeChecked(value: Instant): (State, Seq[Effect]) = {
    unsupported(s"finishTime($value)")
  }

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported from state $name: $message")
  }

  def name: String = {
    ClassUtil.getSimpleClassName(this)
  }
}


object State {

  case object Initial extends State {
    override def readyToGetStarted(expectedQuantity: Int): (State, Seq[Effect]) = {
      val newState = Processing(
        sum = 0,
        expectToProcess = expectedQuantity,
        processed = 0,
        startTime = None)
      val effects = Seq(
        Effect.GetStartedTime,
        Effect.CreateAddEvents(expectedQuantity))
      (newState, effects)
    }
  }

  case class Processing(sum: Int,
                        expectToProcess: Int,
                        processed: Int,
                        startTime: Option[Instant]) extends State {
    override def numberAdded(value: Int): (State, Seq[Effect]) = {
      val newProcessed = processed + 1
      val newValue = sum + value
      val stateAndEffects = if (newProcessed == expectToProcess && startTime.isDefined) {
        val newState = FinishedComputation(
          finalResult = newValue,
          startTime = startTime.get)
        val effects = Seq(Effect.NotifyAdded(value), Effect.GetFinishedTime)
        (newState, effects)
      } else {
        val newState = copy(processed = newProcessed, sum = newValue)
        val effects = Seq(Effect.NotifyAdded(value))
        (newState, effects)
      }
      stateAndEffects
    }

    override def startTimeChecked(value: Instant): (State, Seq[Effect]) = {
      (copy(startTime = Some(value)), Seq())
    }
  }

  case class FinishedComputation(finalResult: Int, startTime: Instant) extends State {
    override def endTimeChecked(value: Instant): (State, Seq[Effect]) = {
      (Done, Seq(Effect.GenerateReport(finalResult, startTime, value), Effect.ResolveDonePromise))
    }
  }

  case object Done extends State

}
