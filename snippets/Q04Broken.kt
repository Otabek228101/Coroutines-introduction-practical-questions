// Question 4 — broken code. NOT part of the Gradle build (it does not compile on purpose).
import kotlinx.coroutines.delay

suspend fun loadUser(): String {
    delay(100)
    return "Ada"
}

fun showUser() {
    val user = loadUser()   // compile error here
    println("User: $user")
}
