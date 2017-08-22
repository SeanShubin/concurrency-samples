package com.seanshubin.concurrency.samples.domain

sealed trait Event

object Event {
  case class Started(id:String, input:BigInt) extends Event
  case class Finished(id:String, input:BigInt, output:BigInt) extends Event
  case class ExpectQuantity(quantity:Int) extends Event
}
