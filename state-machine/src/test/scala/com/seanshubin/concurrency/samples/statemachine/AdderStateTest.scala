package com.seanshubin.concurrency.samples.statemachine

import java.time.{Clock, Instant}

import com.seanshubin.concurrency.samples.statemachine.AdderEffect._
import com.seanshubin.concurrency.samples.statemachine.AdderState.{FinishedComputation, Initial, Processing, ReadyToShutDown}
import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotFinishTime, GotStartTime, ReadyToStart}
import org.scalatest.FunSuite

class AdderStateTest extends FunSuite {
  test("initial -> ready to get started -> get started time and create add events") {
    // given
    val state = Initial
    val expectedQuantity = 123

    // when
    val (newState, effects) = state.apply(ReadyToStart(expectedQuantity))

    // then
    assert(newState === Processing(sum = 0, expectToProcess = 123, processed = 0, startTime = None))
    assert(effects === Seq(GetStartedTime, CreateAddEvents(123)))
  }

  test("unsupported transitions from initial") {
    val state = Initial
    val startTime = Instant.parse("2017-12-08T18:46:10.276Z")
    val finishTime = Instant.parse("2017-12-08T18:46:24.071Z")
    assertUnsupportedTransition(state, AddedNumber(234), "unsupported transition: Initial -> addNumber(234)")
    assertUnsupportedTransition(state, GotStartTime(startTime), "unsupported transition: Initial -> startTime(2017-12-08T18:46:10.276Z)")
    assertUnsupportedTransition(state, GotFinishTime(finishTime), "unsupported transition: Initial -> finishTime(2017-12-08T18:46:24.071Z)")
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
    val (newState, effects) = state.apply(AddedNumber(4))

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
    val (newState, effects) = state.apply(GotStartTime(startTime))

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
    val (newState, effects) = state.apply(AddedNumber(6))

    // then
    assert(newState === FinishedComputation(sum = 9, startTime = startTime))
    assert(effects === Seq(NotifyAdded(6), GetFinishedTime))
  }

  test("finished computation -> end time checked") {
    // given
    val startTime = Instant.parse("2017-12-08T23:42:28.834Z")
    val finishTime = Instant.parse("2017-12-08T23:42:56.790Z")
    val state = FinishedComputation(sum = 20, startTime = startTime)

    // when
    val (newState, effects) = state.apply(GotFinishTime(finishTime))

    // then
    assert(newState === ReadyToShutDown)
    assert(effects === Seq(GenerateReport(20, startTime, finishTime), ResolveDonePromise))
  }

  test("no supported transitions from ReadyToShutDown") {
    println(Clock.systemUTC().instant())
    val state = ReadyToShutDown
    val startTime = Instant.parse("2017-12-08T23:47:05.810Z")
    val finishTime = Instant.parse("2017-12-08T23:47:15.249Z")
    assertUnsupportedTransition(state, ReadyToStart(20), "unsupported transition: ReadyToShutDown -> start(20)")
    assertUnsupportedTransition(state, AddedNumber(200), "unsupported transition: ReadyToShutDown -> addNumber(200)")
    assertUnsupportedTransition(state, GotStartTime(startTime), "unsupported transition: ReadyToShutDown -> startTime(2017-12-08T23:47:05.810Z)")
    assertUnsupportedTransition(state, GotFinishTime(finishTime), "unsupported transition: ReadyToShutDown -> finishTime(2017-12-08T23:47:15.249Z)")
  }

  def assertUnsupportedTransition(state: AdderState, event: Event, expectedMessage: String): Unit = {
    val exception = intercept[RuntimeException] {
      state.apply(event)
    }
    assert(exception.getMessage === expectedMessage)

  }
}
