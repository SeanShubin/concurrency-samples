# Concurrency Samples
- the work
    - [Runner](domain/src/main/scala/com/seanshubin/concurrency/samples/domain/Runner.scala)
    - [PrimeNumberWorker](domain/src/main/scala/com/seanshubin/concurrency/samples/domain/PrimeNumberWorker.scala)
    - [Prime](domain/src/main/scala/com/seanshubin/concurrency/samples/domain/Prime.scala)
    - [State](domain/src/main/scala/com/seanshubin/concurrency/samples/domain/State.scala)
- the concurrency models
    - [not thread safe](not-thread-safe/src/main/scala/com/seanshubin/concurrency/samples/notthreadsafe/StatefulNotThreadSafe.scala)
    - [locking](locking/src/main/scala/com/seanshubin/concurrency/samples/locking/StatefulWithLocking.scala)
    - [atomic reference](atomic-reference/src/main/scala/com/seanshubin/concurrency/samples/atomicreference/StatefulWithAtomicReference.scala)
    - [actor](actor/src/main/scala/com/seanshubin/concurrency/samples/actor/StatefulBehavior.scala)

## Deadlock
- Coffman conditions
    - Mutual Exclusion
    - Hold and Wait
    - No Resource Preemption
    - Circular Wait
- Example
    - Thread A and B need resources C and D
    - A acquires lock on C
    - B acquires lock on D
    - A tries to acquire lock on D
        - A blocks waiting for B to release lock on D
    - B tries to acquire lock on C
        - B blocks waiting for A to release lock on C
