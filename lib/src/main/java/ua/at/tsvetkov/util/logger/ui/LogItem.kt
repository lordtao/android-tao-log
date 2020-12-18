package ua.at.tsvetkov.util.logger.ui

import android.util.Log
import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.interceptor.LogToFileInterceptor
import java.util.*

/**
 * Created by Alexandr Tsvetkov on 08/02/20.
 */
class LogItem {

    var dateString: String = ""
    var level: Level = Level.ERROR
    var tag: String = ""
    var message: String = ""
    lateinit var date: Date

    constructor (logString: StringBuilder) {
        val data = logString.split(LogToFileInterceptor.LOG_SEPARATOR)
        if (data.size == 4) {
            dateString = data[0]
            try {
                date = Date(Date.parse(dateString))
                level = Level.valueOf(data[1])
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.message.toString())
            }
            tag = data[2]
            message = data[3]
        } else {
            Log.e(javaClass.simpleName, "Wrong Log item string: $logString")
        }
    }

    constructor (date: Date, level: Level, tag: String?, msg: String?) {
        this.dateString = date.toString()
        this.level = level
        tag?.let { this.tag = it }
        msg?.let { this.message = it }
        this.date = date
    }

}