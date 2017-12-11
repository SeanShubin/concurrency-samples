package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotFinishTime, GotStartTime, ReadyToStart}

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

sealed trait AdderState extends State[Event] {
  override def apply(event: Event): (AdderState, Seq[AdderEffect]) = {
    event match {
      case ReadyToStart(expectedQuantity) => readyToGetStarted(expectedQuantity)
      case AddedNumber(value) => numberAdded(value)
      case GotStartTime(value) => startTimeChecked(value)
      case GotFinishTime(value) => endTimeChecked(value)
    }
  }

  def readyToGetStarted(expectedQuantity: Int): (AdderState, Seq[AdderEffect]) = {
    unsupported(s"start($expectedQuantity)")
  }

  def numberAdded(value: Int): (AdderState, Seq[AdderEffect]) = {
    unsupported(s"addNumber($value)")
  }

  def startTimeChecked(value: Instant): (AdderState, Seq[AdderEffect]) = {
    unsupported(s"startTime($value)")
  }

  def endTimeChecked(value: Instant): (AdderState, Seq[AdderEffect]) = {
    unsupported(s"finishTime($value)")
  }
}


object AdderState {

  case object Initial extends AdderState {
    override def readyToGetStarted(expectedQuantity: Int): (AdderState, Seq[AdderEffect]) = {
      val newState = Processing(
        sum = 0,
        expectToProcess = expectedQuantity,
        processed = 0,
        startTime = None)
      val effects = Seq(
        AdderEffect.GetStartedTime,
        AdderEffect.CreateAddEvents(expectedQuantity))
      (newState, effects)
    }
  }

  case class Processing(sum: Int,
                        expectToProcess: Int,
                        processed: Int,
                        startTime: Option[Instant]) extends AdderState {
    override def numberAdded(value: Int): (AdderState, Seq[AdderEffect]) = {
      val newProcessed = processed + 1
      val newValue = sum + value
      val stateAndEffects = if (newProcessed == expectToProcess && startTime.isDefined) {
        val newState = FinishedComputation(
          sum = newValue,
          startTime = startTime.get)
        val effects = Seq(AdderEffect.NotifyAdded(value), AdderEffect.GetFinishedTime)
        (newState, effects)
      } else {
        val newState = copy(processed = newProcessed, sum = newValue)
        val effects = Seq(AdderEffect.NotifyAdded(value))
        (newState, effects)
      }
      stateAndEffects
    }

    override def startTimeChecked(value: Instant): (AdderState, Seq[AdderEffect]) = {
      (copy(startTime = Some(value)), Seq())
    }
  }

  case class FinishedComputation(sum: Int, startTime: Instant) extends AdderState {
    override def endTimeChecked(value: Instant): (AdderState, Seq[AdderEffect]) = {
      (ReadyToShutDown, Seq(AdderEffect.GenerateReport(sum, startTime, value), AdderEffect.ResolveDonePromise))
    }
  }

  case object ReadyToShutDown extends AdderState

}
