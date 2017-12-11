package com.seanshubin.concurrency.samples.statemachine

trait State[EventType, EnvironmentType] {
  def apply(event: EventType): (State[EventType, EnvironmentType], Seq[Effect[EventType, EnvironmentType]])

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported transition: $name -> $message")
  }

  def name: String = {
    ClassUtil.getSimpleClassName(this)
  }
}
