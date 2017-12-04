package com.seanshubin.concurrency.samples.statemachine

import com.seanshubin.concurrency.samples.statemachine.Effect.{CreateAddEvents, GetStartedTime}
import com.seanshubin.concurrency.samples.statemachine.State.{Initial, Processing}
import org.scalatest.FunSuite

class StateTest extends FunSuite {
  test("initial -> ready to get started -> get started time and create add events") {
    // given
    val state = Initial
    val expectedQuantity = 123

    // when
    val (newState, effects) = state.readyToGetStarted(expectedQuantity)

    // then
    assert(newState === Processing(sum = 0, expectToProcess = 123, processed = 0, startTime = None))
    assert(effects === Seq(GetStartedTime, CreateAddEvents(123)))
  }
}
