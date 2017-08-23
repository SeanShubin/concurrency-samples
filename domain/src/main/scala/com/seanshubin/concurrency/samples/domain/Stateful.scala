package com.seanshubin.concurrency.samples.domain

trait Stateful {
  def message(event: Event): Unit
}
