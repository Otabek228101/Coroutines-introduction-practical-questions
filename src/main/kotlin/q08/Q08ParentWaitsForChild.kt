package q08

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 8. Parent waits for child
fun main() = runBlocking {
    val parent = launch {
        launch {
            delay(200)
            println("child done")
        }
        println("parent body done")
    }
    parent.join()
    println("report completed")
}
