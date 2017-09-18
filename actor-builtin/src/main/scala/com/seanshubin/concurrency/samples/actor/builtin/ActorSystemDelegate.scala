package com.seanshubin.concurrency.samples.actor.builtin

import java.util.concurrent.ThreadFactory

import akka.actor.{DynamicAccess, Scheduler}
import akka.event.{LoggingAdapter, LoggingFilter}
import akka.typed.patterns.Receptionist
import akka.typed.{ActorRef, ActorSystem, Behavior, Dispatchers, EventStream, Props, Settings, Terminated}
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}

class ActorSystemDelegate[T](delegateToMe: ActorSystem[T]) extends ActorSystemContract[T] {
  override def name: String = delegateToMe.name

  override def settings: Settings = delegateToMe.settings

  override def logConfiguration(): Unit = delegateToMe.logConfiguration()

  override def logFilter: LoggingFilter = delegateToMe.logFilter

  override def log: LoggingAdapter = delegateToMe.log

  override def startTime: Long = delegateToMe.startTime

  override def uptime: Long = delegateToMe.uptime

  override def threadFactory: ThreadFactory = delegateToMe.threadFactory

  override def dynamicAccess: DynamicAccess = delegateToMe.dynamicAccess

  override def scheduler: Scheduler = delegateToMe.scheduler

  override def eventStream: EventStream = delegateToMe.eventStream

  override def dispatchers: Dispatchers = delegateToMe.dispatchers

  override implicit def executionContext: ExecutionContextExecutor = delegateToMe.executionContext

  override def terminate(): Future[Terminated] = delegateToMe.terminate()

  override def whenTerminated: Future[Terminated] = delegateToMe.whenTerminated

  override def deadLetters[U]: ActorRef[U] = delegateToMe.deadLetters

  override def printTree: String = delegateToMe.printTree

  override def systemActorOf[U](behavior: Behavior[U], name: String, props: Props)(implicit timeout: Timeout): Future[ActorRef[U]] = delegateToMe.systemActorOf(behavior, name, props)(timeout)

  override def receptionist: ActorRef[Receptionist.Command] = delegateToMe.receptionist

  override def tell(msg: T): Unit = delegateToMe.tell(msg)
}
