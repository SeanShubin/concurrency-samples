package com.seanshubin.concurrency.samples.domain

case class State(inProgress: Set[String], started: Int, completed: Int, expectedQuantity: Option[Int]) {
  def startWork(name: String): State = {
    val newInProgress = inProgress + name
    val newStarted = started + 1
    copy(inProgress = newInProgress, started = newStarted)
  }

  def finishWork(name: String): State = {
    val newInProgress = inProgress - name
    val newCompleted = completed + 1
    copy(inProgress = newInProgress, completed = newCompleted)
  }

  def expectQuantity(quantity: Int): State = copy(expectedQuantity = Some(quantity))

  def isDone: Boolean = {
    expectedQuantity match {
      case Some(quantity) => completed >= quantity
      case None => false
    }
  }

  def prettyString: String = f"started = $started%2d, completed = $completed%2d, inProgress(${inProgress.size}%2d): ${inProgress.mkString(", ")}%s"
}

object State {
  val Empty = State(inProgress = Set(), started = 0, completed = 0, expectedQuantity = None)
}
