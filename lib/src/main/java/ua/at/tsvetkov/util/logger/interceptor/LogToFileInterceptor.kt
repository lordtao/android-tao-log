package ua.at.tsvetkov.util.logger.interceptor

import android.content.Context
import android.util.Log
import ua.at.tsvetkov.util.logger.ui.LogItem
import ua.at.tsvetkov.util.logger.utils.LogFileWorker
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * Create the Log Interceptor which save a log messages to file (default dir - context.filesDir file Log.txt).
 *
 * @param context
 * @param path path to the file
 * @param name file name
 * @param extension file extencion
 * @param maxLogs max log items in file
 */
class LogToFileInterceptor(val context: Context,
                           val path: String = context.filesDir.absolutePath,
                           val name: String = "log",
                           val extension: String = "txt",
                           val maxLogs: Int = LogFileWorker.MAX_LOGS_IN_FILE) : LogInterceptor, TrunkLongLogDecorInterface {

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
            val verCode = pInfo.versionCode
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
        val message = "${Date()}$LOG_SEPARATOR$level$LOG_SEPARATOR$tag:$LOG_SEPARATOR$msg$LOG_LINE_SEPARATOR\n"
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
    fun pause() {
        enabled = false
        logFileWorker.disabled()
    }

    fun recording() {
        enabled = true
        logFileWorker.enabled()
    }

    fun readToLogArray(logMessages: ArrayList<LogItem>, maxDecorLength: Int, listener: LogOperationListener) {
        logFileWorker.runOperationWithLogFile({ readLogFile(logMessages, maxDecorLength) }, listener)
    }

    private fun readLogFile(logMessages: ArrayList<LogItem>, maxDecorLength: Int) {
        val file = File(fileName)
        if (file.exists()) {
            var reader: BufferedReader? = null
            try {
                val fileReader = FileReader(file)
                reader = BufferedReader(fileReader)
                val sb = StringBuilder()
                for (i in 0 until headerLines) { // Skip header
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