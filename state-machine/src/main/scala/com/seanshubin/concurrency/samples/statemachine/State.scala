package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

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
  def readyToGetStarted(expectedQuantity: Int): StateAndEffects = {
    unsupported(s"start($expectedQuantity)")
  }

  def numberAdded(value: Int): StateAndEffects = {
    unsupported(s"addNumber($value)")
  }

  def startTimeChecked(value: Instant): StateAndEffects = {
    unsupported(s"startTime($value)")
  }

  def endTimeChecked(value: Instant): StateAndEffects = {
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
    override def readyToGetStarted(expectedQuantity: Int): StateAndEffects = {
      val newState = Processing(
        sum = 0,
        expectToProcess = expectedQuantity,
        processed = 0,
        startTime = None)
      val effects = Seq(
        Effect.GetStartedTime,
        Effect.CreateAddEvents(expectedQuantity))
      StateAndEffects(newState, effects)
    }
  }

  case class Processing(sum: Int,
                        expectToProcess: Int,
                        processed: Int,
                        startTime: Option[Instant]) extends State {
    override def numberAdded(value: Int): StateAndEffects = {
      val newProcessed = processed + 1
      val newValue = sum + value
      val stateAndEffects = if (newProcessed == expectToProcess && startTime.isDefined) {
        val newState = FinishedComputation(
          finalResult = sum,
          startTime = startTime.get)
        val effects = Seq(Effect.NotifyAdded(value), Effect.FinishedComputation, Effect.GetFinishedTime)
        StateAndEffects(newState, effects)
      } else {
        val newState = copy(processed = newProcessed, sum = newValue)
        val effects = Seq(Effect.NotifyAdded(value))
        StateAndEffects(newState, effects)
      }
      stateAndEffects
    }

    override def startTimeChecked(value: Instant): StateAndEffects = {
      StateAndEffects(copy(startTime = Some(value)), Seq())
    }
  }

  case class FinishedComputation(finalResult: Int, startTime: Instant) extends State {
    override def endTimeChecked(value: Instant): StateAndEffects = {
      StateAndEffects(Done, Seq(Effect.GenerateReport(finalResult, startTime, value), Effect.ResolveDonePromise))
    }
  }

  case object Done extends State

}
