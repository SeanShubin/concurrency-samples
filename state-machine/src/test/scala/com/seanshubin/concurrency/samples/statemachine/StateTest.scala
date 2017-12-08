package com.seanshubin.concurrency.samples.statemachine

import java.time.Instant

import com.seanshubin.concurrency.samples.statemachine.Effect.{CreateAddEvents, GetStartedTime}
import com.seanshubin.concurrency.samples.statemachine.Event.{AddedNumber, GotFinishTime, GotStartTime, ReadyToStart}
import com.seanshubin.concurrency.samples.statemachine.State.{Initial, Processing}
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

  def assertUnsupportedTransition(state: State, event: Event, expectedMessage: String): Unit = {
    val exception = intercept[RuntimeException] {
      state.applyEvent(event)
    }
    assert(exception.getMessage === expectedMessage)

  }
}
