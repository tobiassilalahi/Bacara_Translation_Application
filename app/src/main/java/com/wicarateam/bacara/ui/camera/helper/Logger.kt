package com.wicarateam.bacara.ui.camera.helper

import android.annotation.SuppressLint
import android.util.Log
import com.wicarateam.bacara.helper.Constant.DEFAULT_MIN_LOG_LEVEL
import com.wicarateam.bacara.helper.Constant.DEFAULT_TAG
import java.util.*

class Logger @JvmOverloads constructor(
    private val tag: String = DEFAULT_TAG,
    messagePrefix: String? = null
) {
    private val messagePrefix: String
    private var minLogLevel = DEFAULT_MIN_LOG_LEVEL

    init {
        val prefix = messagePrefix ?: callerSimpleName
        this.messagePrefix = if (prefix.isNotEmpty()) "$prefix: " else prefix
    }

    private fun isLoggable(logLevel: Int): Boolean {
        return logLevel >= minLogLevel || Log.isLoggable(tag, logLevel)
    }

    private fun toMessage(format: String, vararg args: Any): String {
        return messagePrefix + if (args.isNotEmpty()) String.format(format, *args) else format
    }

    @SuppressLint("LogTagMismatch", "LogNotTimber")
    fun i(format: String, vararg args: Any) {
        if (isLoggable(Log.INFO)) {
            Log.i(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch", "LogNotTimber")
    fun e(format: String, vararg args: Any) {
        if (isLoggable(Log.ERROR)) {
            Log.e(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch", "LogNotTimber")
    fun e(t: Throwable, format: String, vararg args: Any) {
        if (isLoggable(Log.ERROR)) {
            Log.e(tag, toMessage(format, *args), t)
        }
    }

    companion object {
        // Classes to be ignored when examining the stack trace
        private val IGNORED_CLASS_NAMES: MutableSet<String>

        init {
            IGNORED_CLASS_NAMES = HashSet(3)
            IGNORED_CLASS_NAMES.add("dalvik.system.VMStack")
            IGNORED_CLASS_NAMES.add("java.lang.Thread")
            IGNORED_CLASS_NAMES.add(Logger::class.java.canonicalName)
        }

        private val callerSimpleName: String
            get() {
                val stackTrace = Thread.currentThread().stackTrace

                for (elem in stackTrace) {
                    val className = elem.className
                    if (!IGNORED_CLASS_NAMES.contains(className)) {
                        val classParts =
                            className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        return classParts[classParts.size - 1]
                    }
                }

                return Logger::class.java.simpleName
            }
    }
}