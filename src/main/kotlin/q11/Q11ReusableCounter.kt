package q11

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 11. Reusable counter
fun CoroutineScope.startCounter(name: String, limit: Int): Job = launch {
    for (i in 1..limit) {
        delay(100)
        println("$name: $i")
    }
}

fun main() = runBlocking {
    coroutineScope {
        startCounter("A", 3)
        startCounter("B", 5)
        println("launched")
    }
    println("all counters done")
}
