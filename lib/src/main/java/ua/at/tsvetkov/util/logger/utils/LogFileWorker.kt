package ua.at.tsvetkov.util.logger.utils

import android.util.Log
import ua.at.tsvetkov.util.logger.interceptor.LogToFileInterceptor
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Internal File Log Writer for LogToFileInterceptor.
 *
 * @param fileName
 * @param maxLogs maximum number of last saved logs
 */
class LogFileWorker
@JvmOverloads
constructor(val fileName: String, val maxLogs: Int = MAX_LOGS_IN_FILE) {

    val executor: Executor = Executors.newFixedThreadPool(1)
    private var enabled = true
    private var logsCount = 0
    private var file: File? = File(fileName)
    private val oldFileName = "${fileName.substringBeforeLast('.')}.old"

    private var fileWriter: BufferedWriter? = null
    private var cache: ArrayList<String>? = null

    init {
        clear()
        Log.i(javaClass.name, "Started logging to the file $file")
    }

    /**
     * Clear the current log file
     */
    fun clear() {
        file?.delete()
        logsCount = 0
    }

    fun enabled() {
        enabled = true
    }

    fun disabled() {
        enabled = false
        stopWriting()
    }

    fun runOperationWithLogFile(operation: Runnable, listener: LogToFileInterceptor.LogOperationListener? = null) {
        executor.execute {
            stopWriting()
            cache = ArrayList()
            operation.run()
            cache?.forEach { write(it) }
            cache?.clear()
            cache = null
            listener?.onCompleted()
        }
    }

    fun writeAsync(message: String) {
        executor.execute {
            write(message)
        }
    }

    private fun write(message: String) {
        if (!enabled) return
        if (cache != null) {
            cache?.add(message)
            return
        }
        if (logsCount > maxLogs) {
            restrictLogs()
        }
        if (fileWriter == null) {
            fileWriter = BufferedWriter(FileWriter(file, true))
            Log.i("START LOG FILE >>>>", "START WRITING TO ${file?.name}")
        }
        fileWriter?.let {
            try {
                it.write(message)
                logsCount++
            } catch (e: Exception) {
                Log.e(javaClass.name, "Write Log", e)
            }
        }
    }

    private fun restrictLogs() {
        stopWriting()
        val oldFile = File(oldFileName)
        oldFile.delete()
        file?.renameTo(oldFile)
        file = File(fileName)
        logsCount = 0
    }

    private fun stopWriting() {
        fileWriter?.let {
            try {
                it.flush()
                it.close()
                Log.i("CLOSE LOG FILE <$logsCount>>>>", "STOP WRITING TO ${file?.name}")
            } catch (e: Exception) {
                Log.e(javaClass.name, "Restrict Logs", e)
            }
            fileWriter = null
        }
    }

    companion object {
        const val MAX_LOGS_IN_FILE = 10000
    }

}