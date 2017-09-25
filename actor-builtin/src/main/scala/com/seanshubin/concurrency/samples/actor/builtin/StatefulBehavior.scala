package com.seanshubin.concurrency.samples.actor.builtin

import akka.typed.{ActorContext, Behavior, ExtensibleBehavior, Signal}
import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State}

import scala.concurrent.Promise

class StatefulBehavior(notifyThatStateChanged: State => Unit, done: Promise[Unit]) extends ExtensibleBehavior[Event] {
  private var state = State.Empty

  override def receiveSignal(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = this

  override def receiveMessage(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    notifyThatStateChanged(state)
    if (state.isDone) {
      done.success(())
    }
    this
  }
}

/*
           545 milliseconds: started =  0, completed =  0, inProgress( 0):
           586 milliseconds: started =  1, completed =  0, inProgress( 1): work-4300
           586 milliseconds: started =  2, completed =  0, inProgress( 2): work-4300, work-5500
           590 milliseconds: started =  3, completed =  0, inProgress( 3): work-4300, work-5500, work-5700
           592 milliseconds: started =  4, completed =  0, inProgress( 4): work-4300, work-5500, work-5700, work-5000
           657 milliseconds: started =  5, completed =  0, inProgress( 5): work-5000, work-4300, work-5500, work-4100, work-5700
           657 milliseconds: started =  6, completed =  0, inProgress( 6): work-5000, work-4000, work-4300, work-5500, work-4100, work-5700
           657 milliseconds: started =  7, completed =  0, inProgress( 7): work-5000, work-4000, work-4300, work-5500, work-4100, work-4200, work-5700
           658 milliseconds: started =  8, completed =  0, inProgress( 8): work-5000, work-4000, work-5200, work-4300, work-5500, work-4100, work-4200, work-5700
           658 milliseconds: started =  9, completed =  0, inProgress( 9): work-5000, work-4000, work-5200, work-4300, work-5500, work-5900, work-4100, work-4200, work-5700
           659 milliseconds: started = 10, completed =  0, inProgress(10): work-5000, work-4000, work-5200, work-4300, work-5500, work-5900, work-4100, work-4500, work-4200, work-5700
           659 milliseconds: started = 11, completed =  0, inProgress(11): work-5000, work-4000, work-5200, work-4300, work-5500, work-5900, work-4100, work-5800, work-4500, work-4200, work-5700
 5 seconds 878 milliseconds: started = 11, completed =  1, inProgress(10): work-5000, work-5200, work-4300, work-5500, work-5900, work-4100, work-5800, work-4500, work-4200, work-5700
 5 seconds 879 milliseconds: started = 12, completed =  1, inProgress(11): work-5000, work-5200, work-4300, work-5500, work-5900, work-4100, work-5800, work-5100, work-4500, work-4200, work-5700
 6 seconds   5 milliseconds: started = 12, completed =  2, inProgress(10): work-5000, work-5200, work-4300, work-5500, work-5900, work-5800, work-5100, work-4500, work-4200, work-5700
 6 seconds   6 milliseconds: started = 13, completed =  2, inProgress(11): work-5000, work-5200, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-4500, work-4200, work-5700
 6 seconds 220 milliseconds: started = 13, completed =  3, inProgress(10): work-5000, work-5200, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-4500, work-5700
 6 seconds 221 milliseconds: started = 14, completed =  3, inProgress(11): work-5000, work-5200, work-4300, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-5700
 6 seconds 579 milliseconds: started = 14, completed =  4, inProgress(10): work-5000, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-5700
 6 seconds 581 milliseconds: started = 15, completed =  4, inProgress(11): work-5000, work-4400, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-5700
 7 seconds  32 milliseconds: started = 15, completed =  5, inProgress(10): work-5000, work-4400, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 7 seconds  33 milliseconds: started = 16, completed =  5, inProgress(11): work-5000, work-4800, work-4400, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 8 seconds 464 milliseconds: started = 16, completed =  6, inProgress(10): work-4800, work-4400, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 8 seconds 465 milliseconds: started = 17, completed =  6, inProgress(11): work-4900, work-4800, work-4400, work-5200, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 9 seconds 240 milliseconds: started = 17, completed =  7, inProgress(10): work-4900, work-4800, work-4400, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 9 seconds 241 milliseconds: started = 18, completed =  7, inProgress(11): work-4900, work-4800, work-4400, work-4600, work-5300, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
10 seconds 476 milliseconds: started = 18, completed =  8, inProgress(10): work-4900, work-4800, work-4400, work-4600, work-5300, work-5900, work-4700, work-5800, work-5100, work-5700
10 seconds 477 milliseconds: started = 19, completed =  8, inProgress(11): work-4900, work-4800, work-4400, work-4600, work-5300, work-5900, work-5600, work-4700, work-5800, work-5100, work-5700
11 seconds 239 milliseconds: started = 19, completed =  9, inProgress(10): work-4900, work-4800, work-4400, work-4600, work-5300, work-5900, work-5600, work-4700, work-5800, work-5100
11 seconds 240 milliseconds: started = 20, completed =  9, inProgress(11): work-4900, work-4800, work-4400, work-4600, work-5300, work-5900, work-5600, work-4700, work-5800, work-5100, work-5400
11 seconds 748 milliseconds: started = 20, completed = 10, inProgress(10): work-4900, work-4800, work-4400, work-4600, work-5300, work-5900, work-5600, work-4700, work-5100, work-5400
11 seconds 894 milliseconds: started = 20, completed = 11, inProgress( 9): work-4900, work-4800, work-4400, work-4600, work-5300, work-5600, work-4700, work-5100, work-5400
12 seconds 535 milliseconds: started = 20, completed = 12, inProgress( 8): work-4900, work-4800, work-4600, work-5300, work-5600, work-4700, work-5100, work-5400
12 seconds 572 milliseconds: started = 20, completed = 13, inProgress( 7): work-4900, work-4800, work-5300, work-5600, work-4700, work-5100, work-5400
12 seconds 848 milliseconds: started = 20, completed = 14, inProgress( 6): work-4900, work-4800, work-5300, work-5600, work-5100, work-5400
13 seconds 428 milliseconds: started = 20, completed = 15, inProgress( 5): work-4900, work-5300, work-5600, work-5100, work-5400
13 seconds 453 milliseconds: started = 20, completed = 16, inProgress( 4): work-4900, work-5300, work-5600, work-5400
14 seconds  31 milliseconds: started = 20, completed = 17, inProgress( 3): work-5300, work-5600, work-5400
14 seconds 579 milliseconds: started = 20, completed = 18, inProgress( 2): work-5600, work-5400
14 seconds 971 milliseconds: started = 20, completed = 19, inProgress( 1): work-5400
14 seconds 996 milliseconds: started = 20, completed = 20, inProgress( 0):
*/
