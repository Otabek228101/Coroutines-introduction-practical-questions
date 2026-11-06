package q15

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 15. Three exports in a scope
fun main() = runBlocking {
    println("Start")
    coroutineScope {
        launch { delay(300); println("X") }
        launch { delay(100); println("Y") }
        launch { delay(200); println("Z") }
    }
    println("All done")
}
