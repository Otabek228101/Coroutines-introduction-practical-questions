package q05

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Question 5. Suspend is not a thread
suspend fun currentThreadName(): String {
    delay(50)
    return Thread.currentThread().name
}

fun main() = runBlocking {
    println("Before call: ${Thread.currentThread().name}")
    val name = currentThreadName()
    println("Inside suspend fun: $name")
}
