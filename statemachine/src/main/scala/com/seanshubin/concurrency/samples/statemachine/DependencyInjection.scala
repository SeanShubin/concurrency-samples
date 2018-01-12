package com.seanshubin.concurrency.samples.statemachine

import java.time.Clock
import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior}

import scala.concurrent._
import scala.concurrent.duration.Duration

trait DependencyInjection {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val clock: Clock = Clock.systemUTC()
  val donePromise: Promise[Unit] = Promise()
  val system: SystemContract = SystemDelegate
  val environment: Environment = new EnvironmentImpl(donePromise, clock, system)
  val emitLine: String => Unit = println
  val stateMachine: Behavior[Event] = new StateMachine[Event, Environment](environment, AdderState.Initial)
  val actorSystem: ActorSystem[Event] = ActorSystem(stateMachine, "stateMachineActor")
  val actorSystemContract: ActorSystemContract[Event] = new ActorSystemDelegate[Event](actorSystem)
  val duration = Duration(5, TimeUnit.SECONDS)
  val done: Future[Unit] = donePromise.future
  val awaitReady: (Awaitable[Unit], Duration) => Unit = (a, d) => {
    Await.ready(a, d)
  }
  val runner: Runnable = new Runner(
    actorSystemContract,
    done,
    duration
  )
}
