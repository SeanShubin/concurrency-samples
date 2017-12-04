package com.seanshubin.concurrency.samples.statemachine

import java.time.Clock
import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}

trait DependencyInjection {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val clock: Clock = Clock.systemUTC()
  val donePromise: Promise[Unit] = Promise()
  val system: SystemContract = SystemDelegate
  val environment: Environment = new EnvironmentImpl(donePromise, clock, system)
  val emitLine: String => Unit = println
  val eventApplier: EventApplier = new EventApplierImpl
  val stateMachine: Behavior[Event] = new StateMachine(
    environment, eventApplier)
  val actorSystem: ActorSystem[Event] = ActorSystem(stateMachine, "stateMachineActor")
  val duration = Duration(5, TimeUnit.SECONDS)
  val done: Future[Unit] = donePromise.future
  val await: AwaitContract = AwaitDelegate
  val runner: Runnable = new Runner(
    await,
    actorSystem,
    done,
    duration
  )
}
