package com.seanshubin.concurrency.samples.statemachine

trait State[EventType] {
  def apply(event: EventType): (State[EventType], Seq[Effect[EventType]])

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported transition: $name -> $message")
  }

  def name: String = {
    ClassUtil.getSimpleClassName(this)
  }
}
