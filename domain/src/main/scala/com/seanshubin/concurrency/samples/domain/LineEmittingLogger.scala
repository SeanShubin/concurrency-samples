package com.seanshubin.concurrency.samples.domain

class LineEmittingLogger(emit: String => Unit) extends Logger {
  override def stateChanged(state: State): Unit = {
    emit(state.prettyString)
  }
}
