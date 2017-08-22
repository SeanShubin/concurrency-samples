package com.seanshubin.concurrency.samples.atomicreference

import com.seanshubin.concurrency.samples.domain.Cleanup

class NoCleanupNeeded extends Cleanup {
  override def cleanup(): Unit = {
    // nothing to do
  }
}
