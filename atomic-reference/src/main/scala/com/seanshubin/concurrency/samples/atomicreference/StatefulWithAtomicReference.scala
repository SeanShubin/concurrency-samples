package com.seanshubin.concurrency.samples.atomicreference

import java.util.concurrent.atomic.AtomicReference

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.annotation.tailrec
import scala.concurrent.Promise

class StatefulWithAtomicReference(notifyThatStateChanged: State => Unit, done: Promise[Unit]) extends Stateful {
  private val state = new AtomicReference[State](State.Empty)

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => atomicallyTransformState {
        state.get.startWork(id)
      }
      case Finished(id, _, _) => atomicallyTransformState {
        state.get.finishWork(id)
      }
      case ExpectQuantity(quantity) => atomicallyTransformState {
        state.get.expectQuantity(quantity)
      }
    }
  }

  @tailrec
  private def atomicallyTransformState(transformState: => State): Unit = {
    val oldState = state.get()
    val newState = transformState
    if (state.compareAndSet(oldState, newState)) {
      notifyThatStateChanged(newState)
      if (state.get.isDone) {
        done.success(())
      }
    } else {
      atomicallyTransformState(transformState)
    }
  }
}

/*
            69 milliseconds: started =  0, completed =  0, inProgress( 0):
           174 milliseconds: started =  1, completed =  0, inProgress( 1): work-4700
           175 milliseconds: started =  2, completed =  0, inProgress( 2): work-4700, work-5700
           177 milliseconds: started =  3, completed =  0, inProgress( 3): work-4700, work-5700, work-4200
           180 milliseconds: started =  4, completed =  0, inProgress( 4): work-4700, work-5700, work-4200, work-4500
           196 milliseconds: started =  6, completed =  0, inProgress( 6): work-5900, work-4100, work-4700, work-4500, work-4200, work-5700
           196 milliseconds: started =  7, completed =  0, inProgress( 7): work-5000, work-5900, work-4100, work-4700, work-4500, work-4200, work-5700
           196 milliseconds: started =  5, completed =  0, inProgress( 5): work-5900, work-4700, work-4500, work-4200, work-5700
           198 milliseconds: started =  8, completed =  0, inProgress( 8): work-5000, work-5500, work-5900, work-4100, work-4700, work-4500, work-4200, work-5700
           199 milliseconds: started =  9, completed =  0, inProgress( 9): work-5000, work-5500, work-5900, work-4100, work-4700, work-5800, work-4500, work-4200, work-5700
           200 milliseconds: started = 10, completed =  0, inProgress(10): work-5000, work-4000, work-5500, work-5900, work-4100, work-4700, work-5800, work-4500, work-4200, work-5700
 5 seconds 405 milliseconds: started = 10, completed =  1, inProgress( 9): work-5000, work-5500, work-5900, work-4100, work-4700, work-5800, work-4500, work-4200, work-5700
 5 seconds 406 milliseconds: started = 11, completed =  1, inProgress(10): work-5000, work-5500, work-5900, work-4100, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 622 milliseconds: started = 11, completed =  2, inProgress( 9): work-5000, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 623 milliseconds: started = 12, completed =  2, inProgress(10): work-5000, work-4800, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-4200, work-5700
 5 seconds 968 milliseconds: started = 12, completed =  3, inProgress( 9): work-5000, work-4800, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-5700
 5 seconds 971 milliseconds: started = 13, completed =  3, inProgress(10): work-5000, work-4800, work-4300, work-5500, work-5900, work-4700, work-5800, work-5100, work-4500, work-5700
 6 seconds 686 milliseconds: started = 13, completed =  4, inProgress( 9): work-5000, work-4800, work-4300, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 6 seconds 687 milliseconds: started = 14, completed =  4, inProgress(10): work-5000, work-4800, work-4300, work-4600, work-5500, work-5900, work-4700, work-5800, work-5100, work-5700
 7 seconds 333 milliseconds: started = 14, completed =  5, inProgress( 9): work-5000, work-4800, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-5700
 7 seconds 335 milliseconds: started = 15, completed =  5, inProgress(10): work-4900, work-5000, work-4800, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-5700
 8 seconds  15 milliseconds: started = 15, completed =  6, inProgress( 9): work-4900, work-4800, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-5700
 8 seconds  16 milliseconds: started = 16, completed =  6, inProgress(10): work-4900, work-4800, work-5200, work-4300, work-4600, work-5500, work-5900, work-5800, work-5100, work-5700
 9 seconds 972 milliseconds: started = 16, completed =  7, inProgress( 9): work-4900, work-4800, work-5200, work-4300, work-4600, work-5900, work-5800, work-5100, work-5700
 9 seconds 973 milliseconds: started = 17, completed =  7, inProgress(10): work-4900, work-4800, work-5200, work-4300, work-4600, work-5900, work-5600, work-5800, work-5100, work-5700
10 seconds 565 milliseconds: started = 17, completed =  8, inProgress( 9): work-4900, work-4800, work-5200, work-4300, work-4600, work-5900, work-5600, work-5800, work-5100
10 seconds 566 milliseconds: started = 18, completed =  8, inProgress(10): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5900, work-5600, work-5800, work-5100
11 seconds 109 milliseconds: started = 18, completed =  9, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5900, work-5600, work-5100
11 seconds 110 milliseconds: started = 19, completed =  9, inProgress(10): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5900, work-5600, work-5100
11 seconds 413 milliseconds: started = 19, completed = 10, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5100
11 seconds 416 milliseconds: started = 20, completed = 10, inProgress(10): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5100, work-5400
11 seconds 536 milliseconds: started = 20, completed = 11, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4600, work-5300, work-5600, work-5100, work-5400
12 seconds 676 milliseconds: started = 20, completed = 12, inProgress( 8): work-4900, work-4400, work-5200, work-4600, work-5300, work-5600, work-5100, work-5400
13 seconds  39 milliseconds: started = 20, completed = 13, inProgress( 7): work-4900, work-4400, work-5200, work-5300, work-5600, work-5100, work-5400
13 seconds  94 milliseconds: started = 20, completed = 14, inProgress( 6): work-4900, work-4400, work-5200, work-5300, work-5600, work-5400
13 seconds 943 milliseconds: started = 20, completed = 15, inProgress( 5): work-4400, work-5200, work-5300, work-5600, work-5400
14 seconds 846 milliseconds: started = 20, completed = 16, inProgress( 4): work-4400, work-5300, work-5600, work-5400
15 seconds  55 milliseconds: started = 20, completed = 17, inProgress( 3): work-5300, work-5600, work-5400
15 seconds 925 milliseconds: started = 20, completed = 18, inProgress( 2): work-5300, work-5400
15 seconds 953 milliseconds: started = 20, completed = 19, inProgress( 1): work-5400
16 seconds 154 milliseconds: started = 20, completed = 20, inProgress( 0):
*/
