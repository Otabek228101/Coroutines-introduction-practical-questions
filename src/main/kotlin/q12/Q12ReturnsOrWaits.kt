package q12

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 12. Returns at once or waits

// Returns at once: starts a coroutine in the caller's scope and gives back its Job
fun CoroutineScope.startGreeter(name: String) = launch {
    delay(200)
    println("Hello from background, $name")
}

// Waits: coroutineScope returns only when all its children are done
suspend fun greetAll(names: List<String>) = coroutineScope {
    names.forEach { name ->
        launch {
            delay(100)
            println("Hello, $name")
        }
    }
}

fun main() = runBlocking {
    startGreeter("Ada")
    println("after startGreeter")

    greetAll(listOf("Linus", "Grace"))
    println("after greetAll")
}
