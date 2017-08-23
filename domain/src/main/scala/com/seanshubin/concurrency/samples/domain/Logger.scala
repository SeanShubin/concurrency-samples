package com.seanshubin.concurrency.samples.domain

trait Logger {
  def stateChanged(state: State): Unit
}
