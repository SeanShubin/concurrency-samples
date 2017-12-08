package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.Effect.{CreateAddEvents, GetFinishedTime, GetStartedTime, NotifyAdded}
import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotFinishTime, GotStartTime, ReadyToStart}
import com.seanshubin.concurrency.samples.statemachine.State.{FinishedComputation, Initial, Processing}
import org.scalatest.FunSuite

class StateTest extends FunSuite {
  test("initial -> ready to get started -> get started time and create add events") {
    // given
    val state = Initial
    val expectedQuantity = 123

    // when
    val (newState, effects) = state.applyEvent(ReadyToStart(expectedQuantity))

    // then
    assert(newState === Processing(sum = 0, expectToProcess = 123, processed = 0, startTime = None))
    assert(effects === Seq(GetStartedTime, CreateAddEvents(123)))
  }

  test("unsupported transitions from initial") {
    val startTime = Instant.parse("2017-12-08T18:46:10.276Z")
    val finishTime = Instant.parse("2017-12-08T18:46:24.071Z")
    assertUnsupportedTransition(Initial, AddedNumber(234), "unsupported transition: Initial -> addNumber(234)")
    assertUnsupportedTransition(Initial, GotStartTime(startTime), "unsupported transition: Initial -> startTime(2017-12-08T18:46:10.276Z)")
    assertUnsupportedTransition(Initial, GotFinishTime(finishTime), "unsupported transition: Initial -> finishTime(2017-12-08T18:46:24.071Z)")
  }

  test("processing -> number added") {
    // given
    val sum = 1
    val expectToProcess = 10
    val processed = 3
    val startTime = Instant.parse("2017-12-08T22:19:50.915Z")
    val maybeStartTime = Some(startTime)
    val state = Processing(sum, expectToProcess, processed, maybeStartTime)

    // when
    val (newState, effects) = state.applyEvent(AddedNumber(4))

    // then
    assert(newState === Processing(sum = 5, expectToProcess = 10, processed = 4, startTime = Some(startTime)))
    assert(effects === Seq(NotifyAdded(4)))
  }

  test("processing -> start time checked") {
    // given
    val sum = 3
    val expectToProcess = 30
    val processed = 5
    val startTime = Instant.parse("2017-12-08T22:23:29.323Z")
    val maybeStartTime = Some(startTime)
    val state = Processing(sum, expectToProcess, processed, maybeStartTime)

    // when
    val (newState, effects) = state.applyEvent(GotStartTime(startTime))

    // then
    assert(newState === Processing(sum = 3, expectToProcess = 30, processed = 5, startTime = Some(startTime)))
    assert(effects === Seq())
  }

  test("processing -> number added and we have finished processing") {
    // given
    val sum = 3
    val expectToProcess = 5
    val processed = 4
    val startTime = Instant.parse("2017-12-08T22:28:01.666Z")
    val maybeStartTime = Some(startTime)
    val state = Processing(sum, expectToProcess, processed, maybeStartTime)

    // when
    val (newState, effects) = state.applyEvent(AddedNumber(6))

    // then
    assert(newState === FinishedComputation(sum = 9, startTime = startTime))
    assert(effects === Seq(NotifyAdded(6), GetFinishedTime))
  }


  def assertUnsupportedTransition(state: State, event: Event, expectedMessage: String): Unit = {
    val exception = intercept[RuntimeException] {
      state.applyEvent(event)
    }
    assert(exception.getMessage === expectedMessage)

  }
}
