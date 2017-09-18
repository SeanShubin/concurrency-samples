package com.seanshubin.concurrency.samples.actor

import java.util.concurrent.ThreadFactory

import akka.actor.{DynamicAccess, Scheduler}
import akka.event.{LoggingAdapter, LoggingFilter}
import akka.typed.patterns.Receptionist
import akka.typed.{ActorRef, Behavior, Dispatchers, EventStream, Props, Settings, Terminated}
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}

trait ActorSystemContract[T] {
  def name: String

  def settings: Settings

  def logConfiguration(): Unit

  def logFilter: LoggingFilter

  def log: LoggingAdapter

  def startTime: Long

  def uptime: Long

  def threadFactory: ThreadFactory

  def dynamicAccess: DynamicAccess

  def scheduler: Scheduler

  def eventStream: EventStream

  def dispatchers: Dispatchers

  implicit def executionContext: ExecutionContextExecutor

  def terminate(): Future[Terminated]

  def whenTerminated: Future[Terminated]

  def deadLetters[U]: ActorRef[U]

  def printTree: String

  def systemActorOf[U](behavior: Behavior[U], name: String, props: Props)(implicit timeout: Timeout): Future[ActorRef[U]]

  def receptionist: ActorRef[Receptionist.Command]

  def tell(msg: T): Unit
}
