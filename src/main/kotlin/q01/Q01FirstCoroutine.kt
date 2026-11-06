package q01

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 1. First coroutine
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("Coffee ready")
    }
    println("Order accepted")
}
