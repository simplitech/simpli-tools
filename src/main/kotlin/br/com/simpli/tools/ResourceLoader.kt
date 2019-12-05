package br.com.simpli.tools

import java.io.InputStream
import java.util.*

object ResourceLoader {

    fun getProperties(path: String, vararg classes: Class<*>): Properties {
        return Properties().apply {
            getStreamResource(path, *classes)?.also {
                load(it)
            }?.also {
                it.close()
            }
        }
    }

    fun getStreamResource(path: String, vararg classes: Class<*>): InputStream? {
        return Thread.currentThread().contextClassLoader?.getResourceAsStream(path)
            ?: classes.getFirstStream(path)
            ?: this.javaClass.getResourceAsStream(path)
    }

    private fun Array<out Class<*>>.getFirstStream(path: String): InputStream? {

        var stream: InputStream? = null
        this.forEach {
            it.getResourceAsStream(path)?.apply {
                stream = this
                return@forEach
            }
        }

        return stream
    }
}