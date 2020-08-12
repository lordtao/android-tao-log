package ua.at.tsvetkov.util.ui

import android.util.Log
import ua.at.tsvetkov.util.interceptor.Level
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor

/**
 * Created by Alexandr Tsvetkov on 08/02/20.
 */
class LogItem(logString: String) {

    var date: String = ""
    var level: Level = Level.ERROR
    var tag: String = ""
    var message: String = ""

    init {
        val data = logString.split(LogToFileInterceptor.LOG_SEPARATOR).toTypedArray()
        if (data.size == 4) {
            date = data[0]
            setLevel(data[1].trim { it <= ' ' })
            tag = data[2]
            message = data[3]
        } else {
            Log.e(TAG, "Wrong Log item string: $logString")
        }
    }

    private fun setLevel(levelStr: String) {
        try {
            level = Level.valueOf(levelStr)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, e.message + " " + levelStr)
        }
    }

    companion object {
        private const val TAG = "LogItem"
    }

 }