/******************************************************************************
 * Copyright (c) 2010-2025 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * https://opensource.org/license/mit
 *
 * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 *
 * Project:
 * TAO Log
 *
 * License agreement:
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ua.at.tsvetkov.util.logger

import android.content.Context
import ua.at.tsvetkov.util.logger.utils.StringFixedQueue
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A logger class that manages packet logs in memory using a fixed-size queue
 * and optionally duplicates them to Android's Logcat.
 *
 * It is designed to accumulate a batch of logs in memory for later use,
 * either retrieved as a list or printed to Logcat in one go.
 */
class PacketLog(
    val capacity: Int = 500,
    val isEnabled: Boolean = true,
    val duplicateToLogcat: Boolean = false
) {
    private val logDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var memoryLogs: StringFixedQueue? = null

    init {
        if (isEnabled) {
            memoryLogs = StringFixedQueue(capacity)
        }
    }

    /**
     * Clears all logs stored in memory.
     */
    fun clear() {
        memoryLogs?.clear()
    }

    /**
     * Logs an info message.
     */
    fun logI(message: String) = log("INFO   ", message) { Log.i(message) }

    /**
     * Logs a verbose message.
     */
    fun logV(message: String) = log("VERBOSE", message) { Log.v(message) }

    /**
     * Logs a debug message.
     */
    fun logD(message: String) = log("DEBUG  ", message) { Log.d(message) }

    /**
     * Logs a warning message.
     */
    fun logW(message: String) = log("WARN   ", message) { Log.w(message) }

    /**
     * Logs a warning message and a throwable.
     *
     * @param message The message to log.
     * @param e The throwable to log.
     */
    fun logW(message: String, e: Throwable) {
        log("WARN   ", "$message\n${e.stackTraceToString()}") { Log.w(message, e) }
    }

    /**
     * Logs a throwable.
     *
     * @param e The throwable to log.
     */
    fun logW(e: Throwable) {
        log("WARN   ", "$e.message\n${e.stackTraceToString()}") { Log.w(e.message, e) }
    }

    /**
     * Logs an error message.
     */
    fun logE(message: String) = log("ERROR  ", message) { Log.e(message) }

    /**
     * Logs an error message and a throwable.
     *
     * @param message The message to log.
     * @param e The throwable to log.
     */
    fun logE(message: String, e: Throwable) {
        log("ERROR  ", "$message\n${e.stackTraceToString()}") { Log.e(message, e) }
    }

    /**
     * Logs a throwable.
     *
     * @param e The throwable to log.
     */
    fun logE(e: Throwable) {
        log("ERROR  ", "$e.message\n${e.stackTraceToString()}") { Log.e(e.message, e) }
    }

    private fun log(level: String, message: String, logcatAction: (String) -> Unit) {
        if (isEnabled) {
            val timestamp = logDateFormat.format(Date())
            val formattedLog = "$timestamp $level : $message"
            memoryLogs?.add(formattedLog)
        }

        if (duplicateToLogcat) {
            logcatAction(message)
        }
    }

    /**
     * Retrieves all logs currently stored in memory as a List of strings.
     */
    fun getLogs(): List<String> = memoryLogs?.toList() ?: emptyList()

    /**
     * Prints all logs with INFO level to Logcat and clears them.
     *
     * @param title The title for the log output.
     */
    fun printLogs(title: String) {
        LogLong.list(getLogs(), title)
        clear()
    }

    /**
     * Prints all logs with DEBUG level to Logcat and clears them.
     *
     * @param title The title for the log output.
     */
    fun printLogsD(title: String) {
        LogLong.listD(getLogs(), title)
        clear()
    }

    /**
     * Prints all logs with ERROR level to Logcat and clears them.
     *
     * @param title The title for the log output.
     */
    fun printLogsE(title: String) {
        LogLong.listE(getLogs(), title)
        clear()
    }

    /**
     * Prints all logs with VERBOSE level to Logcat and clears them.
     *
     * @param title The title for the log output.
     */
    fun printLogsV(title: String) {
        LogLong.listV(getLogs(), title)
        clear()
    }

    /**
     * Prints all logs with WARN level to Logcat and clears them.
     *
     * @param title The title for the log output.
     */
    fun printLogsW(title: String) {
        LogLong.listW(getLogs(), title)
        clear()
    }/**
     * Writes all logs to a file named after the provided object's class within a 'logs' directory
     * in the application's internal storage. It then optionally clears the in-memory log buffer.
     * This operation is thread-safe and appends to the file.
     * If writing fails, the logs are not cleared.
     *
     * @param obj The object whose class name will be used for the log file.
     * @param context The context needed to access the application's file directory.
     * @param clearAfterWrite If true, clears logs from memory after writing to the file.
     */
    @Synchronized
    fun writeLogsToFile(obj: Any, context: Context, clearAfterWrite: Boolean = false) {
        if (!isEnabled) {
            return
        }
        val logDir = File(context.filesDir, "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val logFile = File(logDir, "${obj.javaClass.simpleName}.txt")
        writeLogsToFile(logFile, clearAfterWrite)
    }

    /**
     * Writes all logs to the specified file and then optionally clears the in-memory log buffer.
     * This operation is thread-safe and appends to the file.
     * If writing fails, the logs are not cleared.
     *
     * @param file The destination file.
     * @param clearAfterWrite If true, clears logs from memory after writing to the file.
     */
    @Synchronized
    fun writeLogsToFile(file: File, clearAfterWrite: Boolean = false) {
        if (!isEnabled) {
            return
        }
        val logsToWrite = getLogs()
        if (logsToWrite.isEmpty()) {
            return
        }

        try {
            file.parentFile?.mkdirs()
            FileWriter(file, true).buffered().use { writer ->
                logsToWrite.forEach { logLine ->
                    writer.write(logLine)
                    writer.newLine()
                }
            }
            if (clearAfterWrite) {
                clear()
            }
        } catch (e: IOException) {
            logE("Failed to write logs to file: ${file.absolutePath}", e)
        }
    }
}
