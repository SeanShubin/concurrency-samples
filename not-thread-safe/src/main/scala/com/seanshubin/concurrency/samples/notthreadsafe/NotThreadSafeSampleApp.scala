package com.seanshubin.concurrency.samples.notthreadsafe

object NotThreadSafeSampleApp extends App {
  new DependencyInjection {}.runner.run()
}
