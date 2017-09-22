package com.seanshubin.concurrency.samples.domain

import java.time.{Clock, Instant}

class LineEmittingLogger(emit: String => Unit, clock: Clock, referenceTime: Instant) extends Logger {
  override def stateChanged(state: State): Unit = {
    val stateString = state.prettyString
    val currentTime = clock.instant()
    val durationMillis = currentTime.toEpochMilli - referenceTime.toEpochMilli
    val durationFormatted = DurationFormat.MillisecondsFormatPadded.format(durationMillis)
    val durationString = JustifyUtil.rightJustify(durationFormatted, 27)
    emit(s"$durationString: $stateString")
  }
}
