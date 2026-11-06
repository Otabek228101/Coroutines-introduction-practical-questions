package q06

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 6. Ten downloads, one thread
fun main() = runBlocking {
    for (i in 1..10) {
        launch {
            println("download $i started on ${Thread.currentThread().name}")
            delay(500)
            println("download $i finished")
        }
    }
}
