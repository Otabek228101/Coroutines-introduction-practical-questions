package extra1

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToLong
import kotlin.system.measureTimeMillis

// Extra 1. Delay or sleep: measure it
fun Long.toHundreds(): Long = (this / 100.0).roundToLong() * 100

fun main() = runBlocking {
    val withDelay = measureTimeMillis {
        coroutineScope {
            repeat(3) { launch { delay(300) } }
        }
    }
    val withSleep = measureTimeMillis {
        coroutineScope {
            repeat(3) { launch { Thread.sleep(300) } }
        }
    }
    println("delay:        ~${withDelay.toHundreds()} ms")
    println("Thread.sleep: ~${withSleep.toHundreds()} ms")
}
