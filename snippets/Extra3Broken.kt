// Extra 3 — broken code. NOT part of the Gradle build (it does not compile on purpose).
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    launch {               // compile error here
        delay(100)
        println("Hello from coroutine")
    }
}
