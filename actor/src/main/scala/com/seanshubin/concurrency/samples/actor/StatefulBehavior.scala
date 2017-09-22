package com.seanshubin.concurrency.samples.actor

import akka.typed.{ActorContext, Behavior, ExtensibleBehavior, Signal}
import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State}

import scala.concurrent.Promise

class StatefulBehavior(stateChanged: State => Unit, done: Promise[Unit]) extends ExtensibleBehavior[Event] {
  private var state = State.Empty

  override def receiveSignal(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = this

  override def receiveMessage(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    stateChanged(state)
    if (state.isDone) {
      done.success(())
    }
    this
  }
}

/*
           514 milliseconds: started =  0, completed =  0, inProgress( 0):
           558 milliseconds: started =  1, completed =  0, inProgress( 1): work-4000
           559 milliseconds: started =  2, completed =  0, inProgress( 2): work-4000, work-4500
           561 milliseconds: started =  3, completed =  0, inProgress( 3): work-4000, work-4500, work-4700
           632 milliseconds: started =  4, completed =  0, inProgress( 4): work-4000, work-4500, work-4700, work-5800
           652 milliseconds: started =  5, completed =  0, inProgress( 5): work-4000, work-4100, work-4700, work-5800, work-4500
           652 milliseconds: started =  6, completed =  0, inProgress( 6): work-5000, work-4000, work-4100, work-4700, work-5800, work-4500
           653 milliseconds: started =  7, completed =  0, inProgress( 7): work-5000, work-4000, work-5500, work-4100, work-4700, work-5800, work-4500
           653 milliseconds: started =  8, completed =  0, inProgress( 8): work-5000, work-4000, work-5500, work-4100, work-4700, work-5800, work-4500, work-5700
           653 milliseconds: started =  9, completed =  0, inProgress( 9): work-5000, work-4000, work-5500, work-4100, work-4700, work-5800, work-4500, work-4200, work-5700
 5 seconds 139 milliseconds: started =  9, completed =  1, inProgress( 8): work-5000, work-5500, work-4100, work-4700, work-5800, work-4500, work-4200, work-5700
 5 seconds 140 milliseconds: started = 10, completed =  1, inProgress( 9): work-5000, work-5500, work-4100, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 427 milliseconds: started = 10, completed =  2, inProgress( 8): work-5000, work-5500, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 428 milliseconds: started = 11, completed =  2, inProgress( 9): work-5000, work-5200, work-5500, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 498 milliseconds: started = 11, completed =  3, inProgress( 8): work-5000, work-5200, work-5500, work-4700, work-5800, work-5100, work-4500, work-5700
 5 seconds 501 milliseconds: started = 12, completed =  3, inProgress( 9): work-5000, work-5200, work-4300, work-5500, work-4700, work-5800, work-5100, work-4500, work-5700
 6 seconds 201 milliseconds: started = 12, completed =  4, inProgress( 8): work-5000, work-5200, work-4300, work-5500, work-4700, work-5800, work-5100, work-5700
 6 seconds 203 milliseconds: started = 13, completed =  4, inProgress( 9): work-5000, work-5200, work-4300, work-4600, work-5500, work-4700, work-5800, work-5100, work-5700
 6 seconds 750 milliseconds: started = 13, completed =  5, inProgress( 8): work-5000, work-5200, work-4300, work-4600, work-5500, work-5800, work-5100, work-5700
 6 seconds 751 milliseconds: started = 14, completed =  5, inProgress( 9): work-5000, work-4800, work-5200, work-4300, work-4600, work-5500, work-5800, work-5100, work-5700
 7 seconds 399 milliseconds: started = 14, completed =  6, inProgress( 8): work-4800, work-5200, work-4300, work-4600, work-5500, work-5800, work-5100, work-5700
 7 seconds 399 milliseconds: started = 15, completed =  6, inProgress( 9): work-4800, work-5200, work-4300, work-4600, work-5300, work-5500, work-5800, work-5100, work-5700
 8 seconds 999 milliseconds: started = 15, completed =  7, inProgress( 8): work-4800, work-5200, work-4300, work-4600, work-5300, work-5800, work-5100, work-5700
                  9 seconds: started = 16, completed =  7, inProgress( 9): work-4800, work-5200, work-4300, work-4600, work-5300, work-5600, work-5800, work-5100, work-5700
 9 seconds 629 milliseconds: started = 16, completed =  8, inProgress( 8): work-4800, work-5200, work-4300, work-4600, work-5300, work-5600, work-5800, work-5100
 9 seconds 629 milliseconds: started = 17, completed =  8, inProgress( 9): work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5800, work-5100
 9 seconds 883 milliseconds: started = 17, completed =  9, inProgress( 8): work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5100
 9 seconds 883 milliseconds: started = 18, completed =  9, inProgress( 9): work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5900, work-5600, work-5100
10 seconds 442 milliseconds: started = 18, completed = 10, inProgress( 8): work-4800, work-4400, work-5200, work-4600, work-5300, work-5900, work-5600, work-5100
10 seconds 443 milliseconds: started = 19, completed = 10, inProgress( 9): work-4800, work-4400, work-5200, work-4600, work-5300, work-5900, work-5600, work-5100, work-5400
11 seconds 972 milliseconds: started = 19, completed = 11, inProgress( 8): work-4800, work-4400, work-5200, work-5300, work-5900, work-5600, work-5100, work-5400
11 seconds 973 milliseconds: started = 20, completed = 11, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-5300, work-5900, work-5600, work-5100, work-5400
12 seconds 231 milliseconds: started = 20, completed = 12, inProgress( 8): work-4900, work-4800, work-4400, work-5200, work-5300, work-5900, work-5600, work-5400
12 seconds 847 milliseconds: started = 20, completed = 13, inProgress( 7): work-4900, work-4800, work-4400, work-5300, work-5900, work-5600, work-5400
12 seconds 935 milliseconds: started = 20, completed = 14, inProgress( 6): work-4900, work-4400, work-5300, work-5900, work-5600, work-5400
14 seconds 310 milliseconds: started = 20, completed = 15, inProgress( 5): work-4900, work-5300, work-5900, work-5600, work-5400
14 seconds 371 milliseconds: started = 20, completed = 16, inProgress( 4): work-4900, work-5900, work-5600, work-5400
15 seconds 446 milliseconds: started = 20, completed = 17, inProgress( 3): work-4900, work-5900, work-5400
15 seconds 883 milliseconds: started = 20, completed = 18, inProgress( 2): work-4900, work-5900
15 seconds 892 milliseconds: started = 20, completed = 19, inProgress( 1): work-5900
16 seconds  90 milliseconds: started = 20, completed = 20, inProgress( 0):
*/
