package ua.at.tsvetkov.util.logger.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.annotation.MainThread
import ua.at.tsvetkov.util.logger.interceptor.LogToFileInterceptor
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


/**
 * Create the Log zipper.
 * You can share the zipped log with help of any external applications. For example by email, google drive and etc.
 *
 * @param logToFileInterceptor
 * @param logName
 * @param zipName
 */
class LogZipper(val logToFileInterceptor: LogToFileInterceptor,
                val logName: String = logToFileInterceptor.logFileWorker.fileName,
                val zipName: String = "${logName.substringBeforeLast('.')}.zip") {

    private val logFileWorker = logToFileInterceptor.logFileWorker

    /**
     * Zip the current log file and share it.
     * This method using hacky way to disable the runtime check FileProvider which used to grant access and prevent
     * FileUriExposedException when Build.VERSION.SDK_INT >= 24. If you don't like the trick, you can zip the log
     * with [zipAsync] and send/share it by any other suitable for your ways.
     * @see <a href="https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed?noredirect=1&lq=1">Stackoverflow link</a>
     * @param activity
     */
    @MainThread
    fun shareZip(activity: Activity, email: String? = null, subject: String? = null) {
        zipAsync(object : ZipListener {

            override fun onZipCreated(zipName: String) {
                activity.runOnUiThread {
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            val method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                            method.invoke(null)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    val file = File(zipName)
                    val uri = Uri.fromFile(file)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "*/*"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    if (subject != null) {
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    } else {
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Zipped log")
                    }
                    email?.let { intent.putExtra(Intent.EXTRA_EMAIL, email) }
                    activity.startActivity(Intent.createChooser(intent, "Share zipped log file"))
                    Log.v(javaClass.name, "Created $zipName")
                }
            }

            override fun onZipError(zipName: String, e: Exception?) {
                Log.e(javaClass.name, "Can't create $zipName", e)
            }

        })
    }

    /**
     * Zip the current log file
     * @param listener
     */
    fun zipAsync(listener: ZipListener) {
        logFileWorker.runOperationWithLogFile({ zip(logName, zipName, listener) })
    }

    private fun zip(file: String, zipName: String, listener: ZipListener) {
        var out: ZipOutputStream? = null
        try {
            out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipName)))
            val data = ByteArray(BUFFER_SIZE)
            val origin = BufferedInputStream(FileInputStream(file), BUFFER_SIZE)
            try {
                val entry = ZipEntry(file.substringAfterLast(File.separator))
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
            } catch (e: Exception) {
                listener.onZipError(zipName, e)
            } finally {
                origin.close()
            }
        } catch (e: Exception) {
            listener.onZipError(zipName, e)
        } finally {
            try {
                out?.close()
                Log.i(javaClass.name, "$file zipped to $zipName")
                listener.onZipCreated(zipName)
            } catch (e: Exception) {
                listener.onZipError(zipName, e)
            }
        }
    }

    interface ZipListener {
        fun onZipCreated(zipName: String)
        fun onZipError(zipName: String, e: Exception?)
    }

    companion object {
        private const val BUFFER_SIZE = 8192
    }

}