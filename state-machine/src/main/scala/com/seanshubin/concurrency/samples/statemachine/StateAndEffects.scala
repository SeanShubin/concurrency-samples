package com.seanshubin.concurrency.samples.statemachine

case class StateAndEffects(state: State, effects: Seq[Effect])
