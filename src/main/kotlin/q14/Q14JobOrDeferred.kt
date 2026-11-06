package q14

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 14. Job or Deferred
fun main() = runBlocking {
    val job: Job = launch {
        delay(100)
        6 * 7          // value is computed and thrown away: launch's lambda returns Unit
    }
    job.join()
    println("launch gives: $job")   // only a handle, no result

    val deferred: Deferred<Int> = async {
        delay(100)
        6 * 7
    }
    println("async gives: ${deferred.await()}")
}
