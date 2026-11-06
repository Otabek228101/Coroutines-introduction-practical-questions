package q13

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// Question 13. Five builders
fun main() {
    val fromRunBlocking: String = runBlocking {
        val job: Job = launch { delay(50) }
        println("launch returned: $job")

        val deferred: Deferred<Int> = async { delay(50); 10 }
        println("async returned: $deferred, await = ${deferred.await()}")

        val fromScope: String = coroutineScope {
            launch { delay(50) }
            "value from coroutineScope"
        }
        println("coroutineScope returned: $fromScope")

        val fromContext: String = withContext(Dispatchers.Default) {
            "value from withContext on ${Thread.currentThread().name}"
        }
        println("withContext returned: $fromContext")

        "value from runBlocking"
    }
    println("runBlocking returned: $fromRunBlocking")
}
