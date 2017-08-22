package com.seanshubin.concurrency.samples.domain

object Prime {
  def nthPrime(index: BigInt): BigInt = {
    if (index == 0) 1
    else if (index == 1) 2
    else nextPrime(Seq(2), 3, index - 2)
  }

  private def nextPrime(soFarSeq: Seq[BigInt], candidate: BigInt, iterations: BigInt): BigInt = {
    def isDivisible(soFar: BigInt): Boolean = candidate.mod(soFar) == 0

    if (soFarSeq.exists(isDivisible)) {
      nextPrime(soFarSeq, candidate + 2, iterations)
    } else {
      if (iterations == 0) {
        candidate
      } else {
        nextPrime(soFarSeq :+ candidate, candidate + 2, iterations - 1)
      }
    }
  }
}
