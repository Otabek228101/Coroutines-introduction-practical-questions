# Practice 7 — Coroutines introduction

Kotlin/JVM, `kotlinx-coroutines-core 1.10.2`, Gradle (Kotlin DSL), JDK 21.

Every question is a separate file with its own `main` in `src/main/kotlin/<package>/`.
Code that must **not** compile (Question 4, Extra 3) is kept in `snippets/` outside the build.

## How to run

In IntelliJ IDEA: open the project and press the green ▶ next to any `main`.

From the terminal:

```bash
./gradlew run -Pmain=q01.Q01FirstCoroutineKt
./gradlew run -Pmain=extra1.Extra1MeasureKt
```

| Task | Main class |
| --- | --- |
| Q1 | `q01.Q01FirstCoroutineKt` |
| Q2 | `q02.Q02ThreeBaristasKt` |
| Q3 | `q03.Q03SuspendChainKt` |
| Q4 | `q04.Q04SuspendFromNormalKt` |
| Q5 | `q05.Q05SuspendIsNotAThreadKt` |
| Q6 | `q06.Q06TenDownloadsKt` |
| Q7 | `q07.Q07OrderOfLaunchKt` |
| Q8 | `q08.Q08ParentWaitsForChildKt` |
| Q9 | `q09.Q09CancelScreenKt` |
| Q10 | `q10.Q10ProfileReadyKt` |
| Q11 | `q11.Q11ReusableCounterKt` |
| Q12 | `q12.Q12ReturnsOrWaitsKt` |
| Q13 | `q13.Q13FiveBuildersKt` |
| Q14 | `q14.Q14JobOrDeferredKt` |
| Q15 | `q15.Q15ThreeExportsKt` |
| Extra 1–5 | `extra1.Extra1MeasureKt`, `extra2.Extra2ThreeLevelsKt`, `extra3.Extra3NoScopeKt`, `extra4.Extra4CompilerViewKt`, `extra5.Extra5GreetEverybodyKt` |

---

## Suspending vs blocking

```text
BLOCKING (Thread.sleep, long loop, blocking IO):
  The thread itself is stuck. While it waits, it can do nothing else:
  no other coroutine, no UI drawing, nothing. 3 waits of 300 ms on one
  thread = 900 ms.

SUSPENDING (delay, await, join, other suspend calls):
  Only the coroutine is paused. Its state (local variables + the place to
  continue) is saved in a Continuation object, and the thread is given back
  to the dispatcher, which runs other coroutines meanwhile. When the wait is
  over, the coroutine is resumed from the same line. 3 waits of 300 ms on one
  thread = ~300 ms.

In short: blocking wastes the thread, suspending frees it.
"suspend" does NOT mean "background thread": it only means "this function
may pause without blocking the thread".
```

---

## Question 1. First coroutine

File: `src/main/kotlin/q01/Q01FirstCoroutine.kt`

Output:
```
Order accepted
Coffee ready
```

**Why "Order accepted" comes first:** `launch` does not run the coroutine right away. It only schedules
the coroutine and returns a `Job` immediately, so the main body continues and prints
"Order accepted". The launched coroutine starts afterwards, reaches `delay(1000)`, suspends for one
second, and only then prints "Coffee ready". `runBlocking` waits for its child before `main` ends.

---

## Question 2. Three baristas

File: `src/main/kotlin/q02/Q02ThreeBaristas.kt`

Output with `delay`:
```
--- with delay ---
Espresso start
Tea start
Latte start
Tea end
Latte end
Espresso end
```

Output with `Thread.sleep`:
```
--- with Thread.sleep ---
Espresso start
Espresso end
Tea start
Tea end
Latte start
Latte end
```

**What the thread does:** with `delay` the thread is released during each wait and runs the other
baristas, so all three overlap (~300 ms total, ends in order 100/200/300); with `Thread.sleep` the one
thread is stuck inside each sleep and can do nothing else, so the drinks are made one after another
(~600 ms total).

---

## Question 3. Chain of suspend functions

File: `src/main/kotlin/q03/Q03SuspendChain.kt`

Output:
```
Login started
1. Loading user...
1. User loaded
2. Checking password...
2. Password OK
3. Opening profile...
3. Profile opened
Login finished
```

**Suspending does not reorder lines:** inside one coroutine, code is sequential. A suspend call
pauses the coroutine and resumes it on the next line when the call is finished, exactly like a normal
function call. "Login finished" is printed only after the whole chain returns.

---

## Question 4. Calling suspend from normal code

Broken code (`snippets/Q04Broken.kt`):
```kotlin
suspend fun loadUser(): String {
    delay(100)
    return "Ada"
}

fun showUser() {
    val user = loadUser()
    println("User: $user")
}
```

Compile error (Kotlin 2.x, K2):
```
Suspend function 'suspend fun loadUser(): String' can only be called from a coroutine or another suspend function.
```
(Older K1 compiler wording: `Suspend function 'loadUser' should be called only from a coroutine or another suspend function`.)

Fixes (`src/main/kotlin/q04/Q04SuspendFromNormal.kt`):

- **Fix 1** — make the caller `suspend` too (`showUserSuspend`).
- **Fix 2** — start a coroutine with `runBlocking` (`showUserBlocking`).

Output:
```
Fix 1 (suspend caller): Ada
Fix 2 (runBlocking): Ada
```

**Never on the Android UI thread: `runBlocking`.** It blocks the current thread until the coroutine
finishes. On the main thread this freezes the UI (no drawing, no touch handling) and, if it lasts
about 5 seconds, Android shows an ANR ("App Not Responding"). On Android you start coroutines with
`lifecycleScope.launch` / `viewModelScope.launch` instead.

---

## Question 5. Suspend is not a thread

File: `src/main/kotlin/q05/Q05SuspendIsNotAThread.kt`

Output:
```
Before call: main
Inside suspend fun: main
```

**The belief is wrong.** The suspend function ran on the same `main` thread as the caller. `suspend`
does not switch threads; only a dispatcher does (for example `withContext(Dispatchers.IO)`).

**Hidden parameter:** the compiler adds a `Continuation<T>` parameter to every suspend function
(Continuation-Passing Style). It stores where to resume and the local state.

---

## Question 6. Ten downloads, one thread

File: `src/main/kotlin/q06/Q06TenDownloads.kt`

Output:
```
download 1 started on main
download 2 started on main
download 3 started on main
download 4 started on main
download 5 started on main
download 6 started on main
download 7 started on main
download 8 started on main
download 9 started on main
download 10 started on main
download 1 finished
download 2 finished
download 3 finished
download 4 finished
download 5 finished
download 6 finished
download 7 finished
download 8 finished
download 9 finished
download 10 finished
```

All ten start before any finishes, and the whole program takes ~500 ms, not 5 s.

**This is concurrency, not parallelism.** Everything runs on one thread (`main`), so at any moment
only one coroutine is actually executing. The downloads make progress together because each one
suspends at `delay` and lets the thread run the next one. Parallelism would need several threads
(cores) executing code at the same physical time, e.g. with `Dispatchers.Default`.

---

## Question 7. Order of launch

File: `src/main/kotlin/q07/Q07OrderOfLaunch.kt`

Prediction = actual output:
```
1
4
2
5
3
```

**Why "2" is not right after "1":** `launch` only schedules the new coroutine on the dispatcher and
returns. `runBlocking` has one thread, and it is busy with the parent body, so the parent continues
and prints "4". At `delay(50)` the parent suspends and gives the thread away → the child runs and
prints "2", then suspends at `delay(100)`. After 50 ms the parent prints "5"; after 100 ms the child
prints "3".

---

## Question 8. Parent waits for child

File: `src/main/kotlin/q08/Q08ParentWaitsForChild.kt`

Output:
```
parent body done
child done
report completed
```

**Rule of structured concurrency:** a parent coroutine is not completed until all its children are
completed. The parent body ends first, but the parent `Job` stays in the "completing" state while the
child runs, so `parent.join()` returns only after "child done".

---

## Question 9. Screen closed, requests cancelled

File: `src/main/kotlin/q09/Q09CancelScreen.kt`

Output:
```
request 1 started
request 2 started
request 1 cleanup
request 2 cleanup
end
```

**Why the children stop:** in structured concurrency cancellation goes down the tree. Cancelling a
parent cancels all its children. Their `delay` throws `CancellationException`, so "finished" is never
printed, but `finally` still runs and prints the cleanup. `cancelAndJoin` waits until both children
are fully done, so "end" is last.

---

## Question 10. Profile ready

File: `src/main/kotlin/q10/Q10ProfileReady.kt`

Output:
```
settings loaded
avatar loaded
Profile ready
end of main
late coroutine done
```

**Difference:** `coroutineScope` is a suspend function that waits for all of its children, so
"Profile ready" is printed only after both loads. The last `launch` is not wrapped in any scope that
waits; `launch` returns at once, so "end of main" is printed immediately and the late coroutine
finishes 100 ms later (`runBlocking` still waits for it before the program exits).

---

## Question 11. Reusable counter

File: `src/main/kotlin/q11/Q11ReusableCounter.kt`

Output:
```
launched
A: 1
B: 1
A: 2
B: 2
A: 3
B: 3
B: 4
B: 5
all counters done
```

**Why the receiver is needed:** `launch` is an extension on `CoroutineScope`, so it can only be called
on a scope. Making `startCounter` an extension on `CoroutineScope` gives it the caller's scope as
`this`: the function returns at once (it does not suspend), and the new coroutine becomes a child of
the caller's scope, so it is waited for and cancelled together with it. Without a receiver we would
have to use `GlobalScope` or create our own scope, which breaks structured concurrency.

---

## Question 12. Returns at once or waits

File: `src/main/kotlin/q12/Q12ReturnsOrWaits.kt`

Output:
```
after startGreeter
Hello, Linus
Hello, Grace
after greetAll
Hello from background, Ada
```

- `CoroutineScope.startGreeter` **returns at once**: "after startGreeter" is the very first line; the
  greeting appears last (200 ms).
- `suspend fun greetAll` with `coroutineScope` **waits**: "after greetAll" is printed only after
  both greetings.

Convention: an extension on `CoroutineScope` starts work and returns; a `suspend` function finishes
all its work before it returns.

---

## Question 13. Five builders

File: `src/main/kotlin/q13/Q13FiveBuilders.kt`

Output (hash codes and worker number change from run to run):
```
launch returned: StandaloneCoroutine{Active}@33833882
async returned: DeferredCoroutine{Active}@6ed3ef1, await = 10
coroutineScope returned: value from coroutineScope
withContext returned: value from withContext on DefaultDispatcher-worker-2
runBlocking returned: value from runBlocking
```

Cheat sheet:

| Builder | Returns | Blocks the thread? | Waits for children? | Is it suspend? |
| --- | --- | --- | --- | --- |
| `launch` | `Job` | No — returns at once | It is the child; its own `Job` completes after its children | No (extension on `CoroutineScope`) |
| `async` | `Deferred<T>` (`await()` gives `T`) | No — returns at once | Same as `launch` | No (extension on `CoroutineScope`) |
| `runBlocking` | `T` (result of the block) | **Yes** — blocks the current thread until done | Yes | No (normal function, bridge from normal code) |
| `coroutineScope` | `T` (result of the block) | No — suspends | Yes | Yes |
| `withContext` | `T` (result of the block) | No — suspends, switches context/dispatcher | Yes | Yes |

---

## Question 14. Job or Deferred

File: `src/main/kotlin/q14/Q14JobOrDeferred.kt`

Output:
```
launch gives: StandaloneCoroutine{Completed}@7cef4e59
async gives: 42
```

**Why launch cannot give a result:** the lambda of `launch` has type `suspend CoroutineScope.() -> Unit`,
so `6 * 7` is calculated and thrown away. `launch` returns a `Job`, which has no value — only the state
(active / completed / cancelled), `join()` and `cancel()`.

**Job vs Deferred:** `Job` is a handle for a coroutine without a result ("fire and forget");
you can wait for it (`join`) or cancel it. `Deferred<T>` is a `Job` that also carries a result:
`await()` suspends until the coroutine finishes and returns `T` (or re-throws its exception).
`Deferred<T>` extends `Job`, so it can also be joined or cancelled.

---

## Question 15. Three exports in a scope

File: `src/main/kotlin/q15/Q15ThreeExports.kt`

Output:
```
Start
Y
Z
X
All done
```

`coroutineScope` waits for all three children by itself — no `join` and no `delay` in the main body.
The exports run at the same time, so they finish in order of their durations (100, 200, 300 ms).

---

## Extra 1. Delay or sleep: measure it

File: `src/main/kotlin/extra1/Extra1Measure.kt`

Output:
```
delay:        ~300 ms
Thread.sleep: ~900 ms
```

**Why the numbers differ on the same thread:** `delay` suspends only the coroutine and frees the thread,
so the three 300 ms waits overlap → ~300 ms. `Thread.sleep` blocks the only thread, so the three waits
happen one after another → ~900 ms. The manager is wrong: it depends on whether the wait blocks or
suspends.

---

## Extra 2. Three levels of coroutines

File: `src/main/kotlin/extra2/Extra2ThreeLevels.kt`

Output:
```
child 1 done
child 2 done
grandchild done
scope done
```

Coroutine tree:
```text
runBlocking
└── coroutineScope
    ├── child 1  ── prints "child 1 done" at ~0 ms (body ends, but Job completes at 200 ms)
    │   └── grandchild ── delay(200) → "grandchild done" at ~200 ms
    └── child 2  ── delay(100) → "child 2 done" at ~100 ms

"scope done" is printed after coroutineScope returns (~200 ms)
```

**Why "scope done" is last:** a coroutine completes only when all its descendants are completed.
Child 1 is not finished until the grandchild is finished, and `coroutineScope` returns only when both
children are finished — so it waits for the deepest and slowest coroutine (the grandchild, 200 ms).

---

## Extra 3. No scope, no launch

Broken code (`snippets/Extra3Broken.kt`):
```kotlin
fun main() {
    launch {
        delay(100)
        println("Hello from coroutine")
    }
}
```

Compile error (Kotlin 2.2):
```
Unresolved reference 'launch'.
```
(`launch` is an extension on `CoroutineScope`, and a plain `main` has no scope receiver, so the
compiler cannot find it. As a consequence it also reports `delay`: "Suspend function 'suspend fun
delay(timeMillis: Long): Unit' can only be called from a coroutine or another suspend function.")

Fix (`src/main/kotlin/extra3/Extra3NoScope.kt`) — `runBlocking`, the builder from the first lecture
example:

Output:
```
main thread: main
runBlocking body finished, waiting for the child...
Hello from coroutine on main
```

**What `runBlocking` does with the current thread:** it creates a `CoroutineScope` and **blocks the
current thread** (here `main`) until the block and all its child coroutines are finished. It uses that
same thread as an event loop to run the coroutines, which is why the child also prints `main`.

---

## Extra 4. What the compiler sees

File: `src/main/kotlin/extra4/Extra4CompilerView.kt`

```kotlin
suspend fun loadUser(id: Int): User {
    delay(100)
    return User(id, "Ada")
}
```

Decompiled signature (Tools → Kotlin → Show Kotlin Bytecode → Decompile):
```java
@Nullable
public static final Object loadUser(int id, @NotNull Continuation $completion)
```

Same signature from `javap` on the compiled class:
```
public static final java.lang.Object loadUser(int, kotlin.coroutines.Continuation<? super extra4.User>);
```

- **Extra parameter:** `Continuation<? super User> $completion`.
- **New return type:** `Object` (instead of `User`).
- **What the extra parameter is for:** it is the callback that resumes the caller. When the function
  suspends, it returns the special marker `COROUTINE_SUSPENDED` (that is why the return type became
  `Object` — it is either a `User` or the marker). The state machine saves locals and the label of the
  next step in the continuation; when `delay` is over, `continuation.resumeWith(result)` continues the
  function from that label and finally delivers the `User` to the caller.

---

## Extra 5. Greet everybody

File: `src/main/kotlin/extra5/Extra5GreetEverybody.kt`

Output:
```
before
Hello, Ada
Hello, Linus
Hello, Grace
after
```

**Why "after" is last:** `greetEverybody` is a `suspend` function built with `coroutineScope`, which
returns only when all launched children are finished. The last child (Grace, 300 ms) finishes last, and
only then does `main` continue and print "after".
