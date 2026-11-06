package q03

import kotlinx.coroutines.delay

// Question 3. Chain of suspend functions
suspend fun main() {
    println("Login started")
    loadUser()
    println("Login finished")
}

suspend fun loadUser() {
    println("1. Loading user...")
    delay(300)
    println("1. User loaded")
    checkPassword()
}

suspend fun checkPassword() {
    println("2. Checking password...")
    delay(200)
    println("2. Password OK")
    openProfile()
}

suspend fun openProfile() {
    println("3. Opening profile...")
    delay(100)
    println("3. Profile opened")
}
