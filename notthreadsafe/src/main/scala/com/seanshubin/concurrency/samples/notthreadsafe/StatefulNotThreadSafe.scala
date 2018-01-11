package com.seanshubin.concurrency.samples.notthreadsafe

import com.seanshubin.concurrency.samples.domain.Event.{ExpectQuantity, Finished, Started}
import com.seanshubin.concurrency.samples.domain.{Event, State, Stateful}

import scala.concurrent.Promise

class StatefulNotThreadSafe(notifyThatStateChanged: State => Unit, done: Promise[Unit]) extends Stateful {
  private var state = State.Empty

  override def message(msg: Event) = {
    msg match {
      case Started(id, _) => state = state.startWork(id)
      case Finished(id, _, _) => state = state.finishWork(id)
      case ExpectQuantity(quantity) => state = state.expectQuantity(quantity)
    }
    notifyThatStateChanged(state)
    if (state.isDone) {
      done.success(())
    }
  }
}

/*
            75 milliseconds: started =  0, completed =  0, inProgress( 0):
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-4700
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-4200
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-4000
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-5800
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-5100
           184 milliseconds: started =  1, completed =  0, inProgress( 1): work-4100
           185 milliseconds: started =  1, completed =  0, inProgress( 1): work-5000
           185 milliseconds: started =  1, completed =  0, inProgress( 1): work-5700
           186 milliseconds: started =  1, completed =  0, inProgress( 1): work-4500
           185 milliseconds: started =  1, completed =  0, inProgress( 1): work-5500
 5 seconds 498 milliseconds: started =  1, completed =  1, inProgress( 1): work-4500
 5 seconds 504 milliseconds: started =  2, completed =  1, inProgress( 2): work-4500, work-5200
 5 seconds 785 milliseconds: started =  2, completed =  2, inProgress( 2): work-4500, work-5200
 5 seconds 792 milliseconds: started =  3, completed =  2, inProgress( 3): work-4500, work-5200, work-4800
 6 seconds  53 milliseconds: started =  3, completed =  3, inProgress( 3): work-4500, work-5200, work-4800
 6 seconds  60 milliseconds: started =  4, completed =  3, inProgress( 4): work-4500, work-5200, work-4800, work-4300
 6 seconds 732 milliseconds: started =  4, completed =  4, inProgress( 3): work-5200, work-4800, work-4300
 6 seconds 733 milliseconds: started =  5, completed =  4, inProgress( 4): work-5200, work-4800, work-4300, work-4600
 7 seconds 247 milliseconds: started =  5, completed =  5, inProgress( 4): work-5200, work-4800, work-4300, work-4600
 7 seconds 277 milliseconds: started =  6, completed =  5, inProgress( 5): work-4900, work-4800, work-5200, work-4300, work-4600
 8 seconds 409 milliseconds: started =  6, completed =  6, inProgress( 5): work-4900, work-4800, work-5200, work-4300, work-4600
 8 seconds 411 milliseconds: started =  7, completed =  6, inProgress( 6): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300
 8 seconds 770 milliseconds: started =  7, completed =  7, inProgress( 6): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300
 8 seconds 772 milliseconds: started =  8, completed =  7, inProgress( 7): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300, work-5400
10 seconds 227 milliseconds: started =  8, completed =  8, inProgress( 7): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300, work-5400
10 seconds 229 milliseconds: started =  9, completed =  8, inProgress( 8): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300, work-5600, work-5400
10 seconds 921 milliseconds: started =  9, completed =  9, inProgress( 8): work-4900, work-4800, work-5200, work-4300, work-4600, work-5300, work-5600, work-5400
10 seconds 922 milliseconds: started = 10, completed =  9, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5400
11 seconds 302 milliseconds: started = 10, completed = 10, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5600, work-5400
11 seconds 303 milliseconds: started = 11, completed = 10, inProgress(10): work-4900, work-4800, work-4400, work-5200, work-4300, work-4600, work-5300, work-5900, work-5600, work-5400
11 seconds 876 milliseconds: started = 11, completed = 11, inProgress( 9): work-4900, work-4800, work-4400, work-5200, work-4600, work-5300, work-5900, work-5600, work-5400
12 seconds 952 milliseconds: started = 11, completed = 12, inProgress( 8): work-4900, work-4400, work-5200, work-4600, work-5300, work-5900, work-5600, work-5400
13 seconds 328 milliseconds: started = 11, completed = 13, inProgress( 7): work-4900, work-4400, work-5200, work-5300, work-5900, work-5600, work-5400
13 seconds 710 milliseconds: started = 11, completed = 14, inProgress( 6): work-4900, work-4400, work-5300, work-5900, work-5600, work-5400
14 seconds 253 milliseconds: started = 11, completed = 15, inProgress( 5): work-4400, work-5300, work-5900, work-5600, work-5400
15 seconds 303 milliseconds: started = 11, completed = 16, inProgress( 4): work-4400, work-5900, work-5600, work-5400
15 seconds 473 milliseconds: started = 11, completed = 17, inProgress( 3): work-5900, work-5600, work-5400
15 seconds 688 milliseconds: started = 11, completed = 18, inProgress( 2): work-5900, work-5600
16 seconds 194 milliseconds: started = 11, completed = 19, inProgress( 1): work-5900
16 seconds 570 milliseconds: started = 11, completed = 20, inProgress( 0):
*/
