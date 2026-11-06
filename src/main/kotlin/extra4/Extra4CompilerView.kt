package extra4

import kotlinx.coroutines.delay

// Extra 4. What the compiler sees
// Open: Tools -> Kotlin -> Show Kotlin Bytecode -> Decompile
data class User(val id: Int, val name: String)

suspend fun loadUser(id: Int): User {
    delay(100)
    return User(id, "Ada")
}

suspend fun main() {
    println(loadUser(1))
}
