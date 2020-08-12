package ua.at.tsvetkov.util.interceptor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import java.io.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Create the Log Interceptor which save a log messages to file (default dir - context.getCacheDir() file Log.txt).
 * You can share the zipped log with help of any external applications. For example by email, google drive and etc.
 * Add index file with log line numbers
 *
 * @param context
 * @param isIndexed is need to create index file
 */
class LogToFileInterceptor
@JvmOverloads
constructor(private val context: Context, var path: String = context.filesDir.absolutePath, val name: String = "log", var extension: String = "txt", val isIndexed: Boolean = false) : LogInterceptor() {

    private var file: File? = null
    private var indexFile: File? = null
    private var index: Long = 0

    val logFileName: String
        get() = path + File.separator + name + '.' + extension
    val zipFileName: String
        get() = context.externalCacheDir.toString() + File.separator + name + ".zip"
    val indexFileName: String
        get() = path + File.separator + name + '.' + INDEX_FILE_EXT

    init {
        file?.delete()
        indexFile?.delete()
        file = File(logFileName)
        Log.i(javaClass.name, "Created new log file $file")
        if (isIndexed) {
            index = 0
            indexFile = File(indexFileName)
            Log.i(javaClass.name, "Created new log indexes file " + indexFile.toString())
        }
        val header = createHeader()
        writeAsync(header)
    }

    interface ZipListener {
        fun onZipCreated(zipFileName: String)
        fun onZipError(zipFileName: String, e: Exception?)
    }

    companion object {
        const val LOG_SEPARATOR = "\u2063"
        const val LOG_END_OF_MESSAGE = "\u200B"
        private const val INDEX_FILE_EXT = "ids"
        private val executor: Executor = Executors.newFixedThreadPool(1)
        private const val BUFFER_SIZE = 8192
    }

    override fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?) {
        val message = """${Date()} $LOG_SEPARATOR$level $LOG_SEPARATOR$tag: $LOG_SEPARATOR$msg
"""
        writeAsync(message)
    }

    /**
     * Clear current log file
     */
    fun clear() {
        file?.delete()
        indexFile?.delete()
    }

    fun startRecord() {
        setEnabled()
    }

    fun stopRecord() {
        setDisabled()
    }

//    private fun

    private fun writeAsync(message: String) {
        executor.execute {
            writeLog(message)
        }
    }

    private fun writeLog(message: String) {
        Log.i("LOG_TO_FILE>", message)
        try {
            val fw = BufferedWriter(FileWriter(file, true))
            fw.write(message)
            fw.flush()
            fw.close()
            if (isIndexed) {
                writeIndex()
                index = (message.length * 2).toLong()
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "WriteLog", e)
        }
    }

    private fun writeIndex() {
        try {
            indexFile?.let {
                val ds = DataOutputStream(BufferedOutputStream(FileOutputStream(it, true)))
                ds.writeLong(index)
                ds.close()
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "WriteLog", e)
        }
    }


    /**
     * Zip the current log file and send it by email
     *
     * @param activity
     */
    fun shareZippedLog(activity: Activity) {
        executor.execute {
            logToZipFile(object : ZipListener {

                override fun onZipCreated(zipFileName: String) {
                    activity.runOnUiThread {
                        val file = File(zipFileName)
                        val uri = Uri.fromFile(file)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "*/*"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Zipped log")
                        activity.startActivity(Intent.createChooser(intent, "Share zipped log file"))
                    }
                }

                override fun onZipError(zipFileName: String, e: Exception?) {
                    Log.e(javaClass.name, "Can't create $zipFileName", e)
                }

            })
        }
    }

    fun logToZipFile(listener: ZipListener) {
        zip(logFileName, zipFileName, listener)
    }

    private fun createHeader(): String {
        val appInfo = context.applicationInfo
        var version = ""
        var verCode = 0
        try {
            val pInfo = context.packageManager.getPackageInfo(appInfo.packageName, 0)
            version = pInfo.versionName
            verCode = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(javaClass.name, "PackageManager.NameNotFoundException", e)
        }
        return """
            Application name: ${appInfo.name}
            Package: ${appInfo.packageName}
            Version: $version
            Version code: $verCode
            ===========================================================

            """.trimIndent()
    }

    private fun zip(file: String, zipFileName: String, listener: ZipListener) {
        var out: ZipOutputStream? = null
        try {
            val name = "$name.$extension"
            out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFileName)))
            val data = ByteArray(BUFFER_SIZE)
            val origin = BufferedInputStream(FileInputStream(file), BUFFER_SIZE)
            try {
                val entry = ZipEntry(name)
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
            } catch (e: Exception) {
                listener.onZipError(zipFileName, e)
            } finally {
                origin.close()
            }
        } catch (e: Exception) {
            listener.onZipError(zipFileName, e)
        } finally {
            try {
                out!!.close()
                Log.i(javaClass.name, "$file zipped to $zipFileName")
                listener.onZipCreated(zipFileName)
            } catch (e: IOException) {
                listener.onZipError(zipFileName, e)
            }
        }
    }

}