package com.seanshubin.concurrency.samples.statemachine

import java.util.concurrent.ThreadFactory

import akka.actor.{DynamicAccess, Scheduler}
import akka.event.{LoggingAdapter, LoggingFilter}
import akka.typed.receptionist.Receptionist
import akka.typed.{ActorRef, Behavior, Dispatchers, EventStream, Props, Settings, Terminated}
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}

class ActorSystemNotImplemented[T] extends ActorSystemContract[T] {
  override def name: String = ???

  override def settings: Settings = ???

  override def logConfiguration(): Unit = ???

  override def logFilter: LoggingFilter = ???

  override def log: LoggingAdapter = ???

  override def startTime: Long = ???

  override def uptime: Long = ???

  override def threadFactory: ThreadFactory = ???

  override def dynamicAccess: DynamicAccess = ???

  override def scheduler: Scheduler = ???

  override def eventStream: EventStream = ???

  override def dispatchers: Dispatchers = ???

  override implicit def executionContext: ExecutionContextExecutor = ???

  override def terminate(): Future[Terminated] = ???

  override def whenTerminated: Future[Terminated] = ???

  override def deadLetters[U]: ActorRef[U] = ???

  override def printTree: String = ???

  override def systemActorOf[U](behavior: Behavior[U], name: String, props: Props)(implicit timeout: Timeout): Future[ActorRef[U]] = ???

  override def receptionist: ActorRef[Receptionist.Command] = ???

  override def tell(msg: T): Unit = ???
}
