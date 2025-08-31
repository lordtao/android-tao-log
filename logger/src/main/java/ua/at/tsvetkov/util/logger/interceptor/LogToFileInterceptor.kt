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
package ua.at.tsvetkov.util.logger.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import ua.at.tsvetkov.util.logger.ui.LogItem
import ua.at.tsvetkov.util.logger.utils.LogFileWorker
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Date

/**
 * Create the Log Interceptor which save a log messages to file (default dir - context.filesDir file Log.txt).
 *
 * @param context
 * @param path path to the file
 * @param name file name
 * @param extension file extencion
 * @param maxLogs max log items in file
 */
class LogToFileInterceptor(
    val context: Context,
    val path: String = context.filesDir.absolutePath,
    val name: String = "log",
    val extension: String = "txt",
    val maxLogs: Int = LogFileWorker.MAX_LOGS_IN_FILE
) : LogInterceptor, TrunkLongLogDecorInterface {

    val fileName = path + File.separator + name + '.' + extension
    val logFileWorker = LogFileWorker(fileName, maxLogs)
    private var header: String = "No app info"
    private var headerLines: Int = 0

    override var enabled: Boolean = true

    init {
        try {
            val appInfo = context.applicationInfo
            val pInfo = context.packageManager.getPackageInfo(appInfo.packageName, 0)
            val version = pInfo.versionName
            val verCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
            header = """===========================================================
Application name: ${appInfo.name}
Package: ${appInfo.packageName}
Version: $version
Version code: $verCode
===========================================================

"""
            headerLines = header.count { char -> char == '\n' }
        } catch (e: Exception) {
            Log.e(javaClass.name, "We got the Exception", e)
        }

        clear()
    }

    override fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?) {
        val message =
            "${Date()}$LOG_SEPARATOR$level$LOG_SEPARATOR$tag:$LOG_SEPARATOR$msg$LOG_LINE_SEPARATOR\n"
        logFileWorker.writeAsync(message)
    }

    /**
     * Clear current log file
     */
    fun clear(isWriteHeader: Boolean = true) {
        logFileWorker.clear()
        if (isWriteHeader) logFileWorker.writeAsync(header)
    }

    /**
     * Pause
     */
    @Suppress("unused")
    fun pause() {
        enabled = false
        logFileWorker.disabled()
    }

    @Suppress("unused")
    fun recording() {
        enabled = true
        logFileWorker.enabled()
    }

    @Suppress("unused")
    fun readToLogArray(
        logMessages: ArrayList<LogItem>,
        maxDecorLength: Int,
        listener: LogOperationListener
    ) {
        logFileWorker.runOperationWithLogFile(
            { readLogFile(logMessages, maxDecorLength) },
            listener
        )
    }

    private fun readLogFile(logMessages: ArrayList<LogItem>, maxDecorLength: Int) {
        val file = File(fileName)
        if (file.exists()) {
            var reader: BufferedReader? = null
            try {
                val fileReader = FileReader(file)
                reader = BufferedReader(fileReader)
                val sb = StringBuilder()
                repeat(headerLines) { // Skip header
                    reader.readLine()
                }
                var line: String
                var isFirstLine = true
                while (reader.ready()) {
                    try {
                        line = reader.readLine()
                        sb.append(trunkLongDecor(line.replace(" ▪ ", ""), maxDecorLength))
                        if (!isFirstLine) {
                            sb.append('\n')
                        }
                        isFirstLine = false
                        if (line.contains(LOG_LINE_SEPARATOR)) {
                            val length = sb.length
                            sb.delete(length - 3, length)
                            logMessages.add(LogItem(sb))
                            sb.clear()
                            isFirstLine = true
                        }
                    } catch (ex: Exception) {
                        Log.e(javaClass.simpleName, "Bad log format", ex)
                    }
                }
            } catch (e: IOException) {
                Log.e(javaClass.simpleName, "ReadLog", e)
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        ua.at.tsvetkov.util.logger.Log.e(javaClass.name, e)
                    }
                }
            }
        }
    }

    interface LogOperationListener {
        fun onCompleted()
    }

    companion object {

        const val LOG_LINE_SEPARATOR = '\u2028'
        const val LOG_SEPARATOR = '\u2063'

        @SuppressLint("StaticFieldLeak")
        private var sharedInstance: LogToFileInterceptor? = null

        fun getSharedInstance(context: Context): LogToFileInterceptor {
            return init(context)
        }

        fun init(context: Context): LogToFileInterceptor {
            if (sharedInstance == null) {
                sharedInstance = LogToFileInterceptor(context)
                ua.at.tsvetkov.util.logger.Log.addInterceptor(sharedInstance!!)
            }
            return sharedInstance!!
        }
    }

}