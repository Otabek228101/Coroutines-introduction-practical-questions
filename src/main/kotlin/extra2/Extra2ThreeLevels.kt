package extra2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Extra 2. Three levels of coroutines
fun main() = runBlocking {
    coroutineScope {
        launch {                       // child 1
            launch {                   // grandchild
                delay(200)
                println("grandchild done")
            }
            println("child 1 done")
        }
        launch {                       // child 2
            delay(100)
            println("child 2 done")
        }
    }
    println("scope done")
}
