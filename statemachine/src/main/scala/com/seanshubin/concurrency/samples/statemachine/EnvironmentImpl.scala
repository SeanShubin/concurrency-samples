package com.seanshubin.concurrency.samples.statemachine

import java.time.{Clock, Instant}

import scala.concurrent.Promise

class EnvironmentImpl(done: Promise[Unit], clock: Clock, system: SystemContract) extends Environment {
  override def emitLine(message: String): Unit = system.out.println(message)

  override def setDone(): Unit = done.success(())

  override def currentTime(): Instant = clock.instant()
}
