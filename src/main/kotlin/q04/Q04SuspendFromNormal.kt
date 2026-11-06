package q04

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Question 4. Calling suspend from normal code
// The broken version is in snippets/Q04Broken.kt (it does not compile on purpose).

suspend fun loadUser(): String {
    delay(100)
    return "Ada"
}

// Fix 1: make the caller a suspend function too
suspend fun showUserSuspend() {
    val user = loadUser()
    println("Fix 1 (suspend caller): $user")
}

// Fix 2: start a coroutine with runBlocking (blocks the current thread!)
fun showUserBlocking() = runBlocking {
    val user = loadUser()
    println("Fix 2 (runBlocking): $user")
}

suspend fun main() {
    showUserSuspend()
    showUserBlocking()
}
