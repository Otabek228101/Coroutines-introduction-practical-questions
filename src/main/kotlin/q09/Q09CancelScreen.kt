package q09

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 9. Screen closed, requests cancelled
fun main() = runBlocking {
    val screen = launch {
        launch {
            try {
                println("request 1 started")
                delay(1000)
                println("request 1 finished")
            } finally {
                println("request 1 cleanup")
            }
        }
        launch {
            try {
                println("request 2 started")
                delay(1000)
                println("request 2 finished")
            } finally {
                println("request 2 cleanup")
            }
        }
    }
    delay(100)
    screen.cancelAndJoin()
    println("end")
}
