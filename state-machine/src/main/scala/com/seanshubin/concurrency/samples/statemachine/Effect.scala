package com.seanshubin.concurrency.samples.statemachine

trait Effect[EventType, EnvironmentType] {
  def apply(environment: EnvironmentType, eventListener: EventType => Unit): Unit
}
