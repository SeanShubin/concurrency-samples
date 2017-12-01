package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

sealed trait State {
  def start(expectedQuantity: Int): StateAndEffects = {
    unsupported(s"start($expectedQuantity)")
  }

  def addNumber(value: Int): StateAndEffects = {
    unsupported(s"addNumber($value)")
  }

  def startTime(value: Instant): StateAndEffects = {
    unsupported(s"startTime($value)")
  }

  def finishTime(value: Instant): StateAndEffects = {
    unsupported(s"finishTime($value)")
  }

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported from state $name: $message")
  }

  def name: String = {
    val possiblyMessySimpleName = getClass.getSimpleName
    val indexOfDollar = possiblyMessySimpleName.indexOf('$')
    val simpleName = if (indexOfDollar == -1) {
      possiblyMessySimpleName
    } else {
      possiblyMessySimpleName.substring(0, indexOfDollar)
    }
    simpleName
  }
}


object State {

  case object Initial extends State {
    override def start(expectedQuantity: Int): StateAndEffects = {
      val newState = Processing(
        sum = 0,
        expectToProcess = expectedQuantity,
        processed = 0,
        startTime = None)
      val effects = Seq(Effect.NotifyStarted(expectedQuantity))
      StateAndEffects(newState, effects)
    }
  }

  case class Processing(sum: Int,
                        expectToProcess: Int,
                        processed: Int, startTime: Option[Instant]) extends State {
    override def addNumber(value: Int): StateAndEffects = {
      val newProcessed = processed + 1
      val newValue = sum + value
      val newState = copy(processed = newProcessed, sum = newValue)
      val result = if (newProcessed == expectToProcess) {
        StateAndEffects(newState, Seq(Effect.NotifyFinished, Effect.ResolveDonePromise))
      } else {
        StateAndEffects(newState, Seq(Effect.NotifyAdded(value)))
      }
      result
    }

    override def startTime(value: Instant): StateAndEffects = {
      StateAndEffects(copy(startTime = Some(value)), Seq())
    }
  }

}