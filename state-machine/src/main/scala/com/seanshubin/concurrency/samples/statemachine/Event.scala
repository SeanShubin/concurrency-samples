package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

sealed trait Event

object Event {

  case class Start(expectedQuantity: Int) extends Event

  case class AddNumber(value: Int) extends Event

  case class GotStartTime(value: Instant) extends Event

  case class GotFinishTime(value: Instant) extends Event

}
