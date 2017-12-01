package com.seanshubin.concurrency.samples.statemachine

import java.time.{Clock, Instant}

import scala.concurrent.Promise

class EnvironmentImpl(done: Promise[Unit], clock: Clock) extends Environment {
  override def emitLine(message: String): Unit = {
    println(message)
  }

  override def setDone(): Unit = {
    println("setting done")
    done.success(())
  }

  override def currentTime(): Instant = clock.instant()
}
