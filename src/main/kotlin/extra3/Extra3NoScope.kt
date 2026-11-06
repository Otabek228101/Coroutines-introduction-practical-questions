package extra3

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Extra 3. No scope, no launch — fixed with runBlocking
// The broken version is in snippets/Extra3Broken.kt.
fun main() = runBlocking {
    println("main thread: ${Thread.currentThread().name}")
    launch {
        delay(100)
        println("Hello from coroutine on ${Thread.currentThread().name}")
    }
    println("runBlocking body finished, waiting for the child...")
}
