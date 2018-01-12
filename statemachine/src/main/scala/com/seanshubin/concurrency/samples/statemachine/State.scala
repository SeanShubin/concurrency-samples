package com.seanshubin.concurrency.samples.statemachine

trait State[EventType, EnvironmentType] {
  def apply(event: EventType): (State[EventType, EnvironmentType], Seq[Effect[EventType, EnvironmentType]])

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported transition: $stateName -> $message")
  }

  def stateName: String = {
    ClassUtil.getSimpleClassName(this)
  }
}
