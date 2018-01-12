package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

sealed trait Event

object Event {

  case class ReadyToStart(expectedQuantity: Int) extends Event

  case class AddedNumber(value: Int) extends Event

  case class GotStartTime(value: Instant) extends Event

  case class GotFinishTime(value: Instant) extends Event

}
