package com.seanshubin.concurrency.samples.locking

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.concurrent.Promise

class StatefulWithLocking(notifyThatStateChanged: State => Unit, done: Promise[Unit]) extends Stateful {
  private var state = State.Empty

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => atomicallyTransformState {
        state.startWork(id)
      }
      case Finished(id, _, _) => atomicallyTransformState {
        state.finishWork(id)
      }
      case ExpectQuantity(quantity) => atomicallyTransformState {
        state.expectQuantity(quantity)
      }
    }
  }

  private def atomicallyTransformState(transformState: => State): Unit = {
    synchronized {
      state = transformState
      notifyThatStateChanged(state)
      if (state.isDone) {
        done.success(())
      }
    }
  }
}

/*
            71 milliseconds: started =  0, completed =  0, inProgress( 0):
           182 milliseconds: started =  1, completed =  0, inProgress( 1): work-4200
           184 milliseconds: started =  2, completed =  0, inProgress( 2): work-4200, work-4100
           186 milliseconds: started =  3, completed =  0, inProgress( 3): work-4200, work-4100, work-4500
           189 milliseconds: started =  4, completed =  0, inProgress( 4): work-4200, work-4100, work-4500, work-5500
           209 milliseconds: started =  5, completed =  0, inProgress( 5): work-4400, work-5500, work-4100, work-4500, work-4200
           210 milliseconds: started =  6, completed =  0, inProgress( 6): work-4000, work-4400, work-5500, work-4100, work-4500, work-4200
           210 milliseconds: started =  7, completed =  0, inProgress( 7): work-5000, work-4000, work-4400, work-5500, work-4100, work-4500, work-4200
           211 milliseconds: started =  8, completed =  0, inProgress( 8): work-5000, work-4000, work-4400, work-4300, work-5500, work-4100, work-4500, work-4200
           212 milliseconds: started =  9, completed =  0, inProgress( 9): work-5000, work-4000, work-4400, work-5200, work-4300, work-5500, work-4100, work-4500, work-4200
 4 seconds 916 milliseconds: started =  9, completed =  1, inProgress( 8): work-5000, work-4400, work-5200, work-4300, work-5500, work-4100, work-4500, work-4200
 4 seconds 918 milliseconds: started = 10, completed =  1, inProgress( 9): work-5000, work-4400, work-5200, work-4300, work-5500, work-4100, work-5100, work-4500, work-4200
 5 seconds 176 milliseconds: started = 10, completed =  2, inProgress( 8): work-5000, work-4400, work-5200, work-4300, work-5500, work-5100, work-4500, work-4200
 5 seconds 177 milliseconds: started = 11, completed =  2, inProgress( 9): work-5000, work-4400, work-5200, work-4300, work-4600, work-5500, work-5100, work-4500, work-4200
 5 seconds 214 milliseconds: started = 11, completed =  3, inProgress( 8): work-5000, work-4400, work-5200, work-4300, work-4600, work-5500, work-5100, work-4500
 5 seconds 216 milliseconds: started = 12, completed =  3, inProgress( 9): work-5000, work-4400, work-5200, work-4300, work-4600, work-5500, work-4700, work-5100, work-4500
 5 seconds 546 milliseconds: started = 12, completed =  4, inProgress( 8): work-5000, work-4400, work-5200, work-4600, work-5500, work-4700, work-5100, work-4500
 5 seconds 547 milliseconds: started = 13, completed =  4, inProgress( 9): work-5000, work-4400, work-5200, work-4600, work-5500, work-5600, work-4700, work-5100, work-4500
 5 seconds 889 milliseconds: started = 13, completed =  5, inProgress( 8): work-5000, work-5200, work-4600, work-5500, work-5600, work-4700, work-5100, work-4500
 5 seconds 897 milliseconds: started = 14, completed =  5, inProgress( 9): work-5000, work-5200, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100, work-4500
 5 seconds 907 milliseconds: started = 14, completed =  6, inProgress( 8): work-5000, work-5200, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100
 5 seconds 909 milliseconds: started = 15, completed =  6, inProgress( 9): work-5000, work-4800, work-5200, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100
 7 seconds 261 milliseconds: started = 15, completed =  7, inProgress( 8): work-4800, work-5200, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100
 7 seconds 262 milliseconds: started = 16, completed =  7, inProgress( 9): work-4900, work-4800, work-5200, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100
 7 seconds 841 milliseconds: started = 16, completed =  8, inProgress( 8): work-4900, work-4800, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100
 7 seconds 842 milliseconds: started = 17, completed =  8, inProgress( 9): work-4900, work-4800, work-4600, work-5300, work-5500, work-5600, work-4700, work-5100, work-5400
 8 seconds 750 milliseconds: started = 17, completed =  9, inProgress( 8): work-4900, work-4800, work-4600, work-5300, work-5600, work-4700, work-5100, work-5400
 8 seconds 752 milliseconds: started = 18, completed =  9, inProgress( 9): work-4900, work-4800, work-4600, work-5300, work-5600, work-4700, work-5100, work-5400, work-5700
11 seconds  66 milliseconds: started = 18, completed = 10, inProgress( 8): work-4900, work-4800, work-5300, work-5600, work-4700, work-5100, work-5400, work-5700
11 seconds  67 milliseconds: started = 19, completed = 10, inProgress( 9): work-4900, work-4800, work-5300, work-5600, work-4700, work-5800, work-5100, work-5400, work-5700
11 seconds 207 milliseconds: started = 19, completed = 11, inProgress( 8): work-4900, work-4800, work-5300, work-5600, work-5800, work-5100, work-5400, work-5700
11 seconds 211 milliseconds: started = 20, completed = 11, inProgress( 9): work-4900, work-4800, work-5300, work-5900, work-5600, work-5800, work-5100, work-5400, work-5700
11 seconds 936 milliseconds: started = 20, completed = 12, inProgress( 8): work-4900, work-4800, work-5300, work-5900, work-5600, work-5800, work-5400, work-5700
12 seconds 225 milliseconds: started = 20, completed = 13, inProgress( 7): work-4900, work-5300, work-5900, work-5600, work-5800, work-5400, work-5700
13 seconds 414 milliseconds: started = 20, completed = 14, inProgress( 6): work-5300, work-5900, work-5600, work-5800, work-5400, work-5700
13 seconds 497 milliseconds: started = 20, completed = 15, inProgress( 5): work-5900, work-5600, work-5800, work-5400, work-5700
13 seconds 798 milliseconds: started = 20, completed = 16, inProgress( 4): work-5900, work-5800, work-5400, work-5700
14 seconds 365 milliseconds: started = 20, completed = 17, inProgress( 3): work-5900, work-5800, work-5700
15 seconds   7 milliseconds: started = 20, completed = 18, inProgress( 2): work-5900, work-5800
15 seconds 553 milliseconds: started = 20, completed = 19, inProgress( 1): work-5900
15 seconds 679 milliseconds: started = 20, completed = 20, inProgress( 0):
*/
