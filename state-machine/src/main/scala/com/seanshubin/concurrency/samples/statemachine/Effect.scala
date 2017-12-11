package com.seanshubin.concurrency.samples.statemachine

trait Effect[EventType] {
  def apply(environment: Environment, eventListener: EventType => Unit): Unit
}
