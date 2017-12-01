package com.seanshubin.concurrency.samples.statemachine

import java.time.{Duration, Instant}

import akka.typed.Signal

class LineEmittingNotifications(emitLine: String => Unit) extends Notifications {
  def finished(startTime: Instant, endTime: Instant): Unit = {
    val duration = Duration.between(startTime, endTime).toMillis
    val durationString = DurationFormat.MillisecondsFormat.format(duration)
    emitLine(durationString)

  }

  def receivedSignal(signal: Signal): Unit = {
    emitLine(s"signal = $signal")
  }

  def receivedEvent(event: Event): Unit = {
    emitLine(s"event = $event")
  }
}
