package ua.at.tsvetkov.util.interceptor

import android.util.Log

class LogCatInterceptor : LogInterceptor() {

    override fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?) {
        when (level) {
            Level.VERBOSE -> {
                Log.v(tag, msg ?: "null")
            }
            Level.INFO -> {
                Log.i(tag, msg ?: "null")
            }
            Level.DEBUG -> {
                Log.d(tag, msg ?: "null")
            }
            Level.WARNING -> {
                Log.w(tag, msg ?: "null")
            }
            Level.ERROR -> {
                Log.e(tag, msg ?: "null")
            }
            Level.WTF -> {
                Log.wtf(tag, msg ?: "null")
            }
        }
    }

}