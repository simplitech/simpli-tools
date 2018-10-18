package br.com.simpli.tools

import kotlinx.coroutines.experimental.*
import java.lang.Runnable

class CoroutineProxy {
    companion object {
        @JvmStatic
        fun asyncIt(runnable: Runnable) {
            GlobalScope.launch { runnable.run() }
        }
    }
}