package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

trait Environment {
  def emitLine(message: String): Unit

  def setDone(): Unit

  def currentTime(): Instant
}
