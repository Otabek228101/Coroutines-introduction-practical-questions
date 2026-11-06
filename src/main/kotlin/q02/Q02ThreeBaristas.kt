package q02

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 2. Three baristas
fun main(): Unit = runBlocking {
    println("--- with delay ---")
    coroutineScope {
        launch { println("Espresso start"); delay(300); println("Espresso end") }
        launch { println("Tea start"); delay(100); println("Tea end") }
        launch { println("Latte start"); delay(200); println("Latte end") }
    }
    println("--- with Thread.sleep ---")
    coroutineScope {
        launch { println("Espresso start"); Thread.sleep(300); println("Espresso end") }
        launch { println("Tea start"); Thread.sleep(100); println("Tea end") }
        launch { println("Latte start"); Thread.sleep(200); println("Latte end") }
    }
}
