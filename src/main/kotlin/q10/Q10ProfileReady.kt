package q10

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 10. Profile ready
fun main() = runBlocking {
    coroutineScope {
        launch { delay(200); println("avatar loaded") }
        launch { delay(100); println("settings loaded") }
    }
    println("Profile ready")

    launch { delay(100); println("late coroutine done") }
    println("end of main")
}
