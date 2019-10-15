package br.com.simpli.tools

import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.Callable

class CoroutineProxy {
    companion object {
        @JvmStatic
        fun asyncIt(runnable: Runnable) {
            GlobalScope.launch { runnable.run() }
        }

        @JvmStatic
        fun <T :Any> runInParallel(vararg callable: Callable<T>): List<T> {
            return runBlocking<List<T>> {
                callable.map { GlobalScope.async { it.call() } }.awaitAll()
            }
        }

        @JvmStatic
        fun <T :Any> runInParallel(callable: List<Callable<T>>): List<T> {
            return this.runInParallel(*callable.toTypedArray())
        }
    }
}