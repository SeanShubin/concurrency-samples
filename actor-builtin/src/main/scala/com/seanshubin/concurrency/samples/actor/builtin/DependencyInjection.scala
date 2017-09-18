package com.seanshubin.concurrency.samples.actor.builtin

import akka.typed.{ActorSystem, Behavior}
import com.seanshubin.concurrency.samples.domain._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  val executionContext: ExecutionContext = Implicits.global
  val futureRunner: FutureRunner = new FutureRunnerWithExecutionContext(executionContext)
  val emit: String => Unit = println
  val logger: Logger = new LineEmittingLogger(emit)
  val done: Promise[Unit] = Promise()
  val stateful: Behavior[Event] = new StatefulBehavior(logger.stateChanged, done)
  val eventActorSystem: ActorSystem[Event] = ActorSystem.create(stateful, "state")
  val eventActorSystemContract: ActorSystemContract[Event] = new ActorSystemDelegate[Event](eventActorSystem)
  val worker: Worker = new PrimeNumberWorker(futureRunner, eventActorSystem.!)
  val cleanup: Cleanup = new CleanupActorSystems(eventActorSystemContract)
  val runner: Runnable = new Runner(worker, done.future, cleanup)
}
