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

import ua.at.tsvetkov.util.logger.Log.array
import ua.at.tsvetkov.util.logger.Log.list
import ua.at.tsvetkov.util.logger.Log.map
import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.interceptor.LogCatInterceptor
import ua.at.tsvetkov.util.logger.interceptor.LogInterceptor
import ua.at.tsvetkov.util.logger.utils.Format
import ua.at.tsvetkov.util.logger.utils.Format.addMessage
import ua.at.tsvetkov.util.logger.utils.Format.addStackTrace
import ua.at.tsvetkov.util.logger.utils.Format.addThreadInfo
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedMessage
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedThrowable
import kotlin.collections.set

/**
 * Extended logger. Allows you to automatically adequately logged class, method and line call in the log. Makes it easy to write logs. For
 * example Log.v("Message") will in the log some the record:
 * <p>
 * 04-04 08:29:40.336: V > SomeClass: someMethod: 286  Message
 *
 * @author A.Tsvetkov 2010 https://github.com/lordtao/
 */
object Log {

    /**
     * Is log have the line boundaries.
     */
    @Volatile
    @JvmStatic
    var isLogOutlined = true

    /**
     * Send a VERBOSE log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun v(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.VERBOSE, data.tag, getFormattedMessage(data, message).toString())
    }

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun d(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.DEBUG, data.tag, getFormattedMessage(data, message).toString())
    }

    /**
     * Send a INFO log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun i(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedMessage(data, message).toString())
    }

    /**
     * Send a WARN log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun w(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.WARNING, data.tag, getFormattedMessage(data, message).toString())
    }

    /**
     * Send a ERROR log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun e(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.ERROR, data.tag, getFormattedMessage(data, message).toString())
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call
     * stack. Depending on system configuration, a report may be added to the DropBoxManager and/or the process may be terminated immediately
     * with an error dialog.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun wtf(message: String?) {
        val data = Format.getLocationContainer()
        logToAll(Level.WTF, data.tag, getFormattedMessage(data, message).toString())
    }

    // ==========================================================
    /**
     * Send a VERBOSE log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun v(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.VERBOSE, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * Send a DEBUG log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun d(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.DEBUG, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * Send a INFO log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun i(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * Send a WARN log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun w(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.WARNING, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * Send a ERROR log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun e(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.ERROR, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * Send a ERROR log message and log the throwable. RuntimeException is not handled.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun rt(message: String?, tr: Throwable) {
        if (tr is RuntimeException) {
            throw RuntimeException(tr)
        }
        val data = Format.getLocationContainer()
        logToAll(Level.ERROR, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun wtf(message: String?, tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.WTF, data.tag, getFormattedThrowable(data, message, tr).toString(), tr)
    }

    // ==========================================================
    /**
     * Send a VERBOSE log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun v(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.VERBOSE, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * Send a DEBUG log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun d(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.DEBUG, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * Send a INFO log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun i(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * Send a WARN log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun w(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.WARNING, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * Send a ERROR log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun e(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.ERROR, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * Send a ERROR log the throwable. RuntimeException is not handled.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun rt(tr: Throwable) {
        if (tr is RuntimeException) {
            throw RuntimeException(tr)
        }
        val data = Format.getLocationContainer()
        logToAll(Level.ERROR, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun wtf(tr: Throwable) {
        val data = Format.getLocationContainer()
        logToAll(Level.WTF, data.tag, getFormattedThrowable(data, tr).toString(), tr)
    }

    // =========================== Collections, arrays and objects ===============================

    /**
     * Logged String representation of map. Each item in new line.
     *
     * @param map a Map
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    fun map(map: Map<*, *>?, title: String? = "Map") {
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedMessage(data, Format.map(map), title).toString())
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    fun list(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.list(list), title).toString()
        )
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun listD(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        logToAll(
            Level.DEBUG,
            data.tag,
            getFormattedMessage(data, Format.list(list), title).toString()
        )
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun listV(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        logToAll(
            Level.VERBOSE,
            data.tag,
            getFormattedMessage(data, Format.list(list), title).toString()
        )
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun listW(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        logToAll(
            Level.WARNING,
            data.tag,
            getFormattedMessage(data, Format.list(list), title).toString()
        )
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun listE(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        logToAll(
            Level.ERROR,
            data.tag,
            getFormattedMessage(data, Format.list(list), title).toString()
        )
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     */
    @JvmStatic
    fun <T : Any> array(array: Array<T>?) {
        array(array, Format.ARRAY)
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     * @param title a title string
     */
    @JvmStatic
    fun <T : Any> array(array: Array<T>?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.arrayT(array), title).toString()
        )
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun array(array: Array<String>?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.arrayString(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: IntArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: FloatArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: BooleanArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: CharArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: DoubleArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), title).toString()
        )
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: LongArray?, title: String? = Format.ARRAY) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.array(array), message = null, title).toString()
        )
    }

    /**
     * Logged String representation of class.
     *
     * @param obj a class for representation
     */
    @JvmStatic
    fun classInfo(obj: Any) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.classInfo(obj), obj.javaClass.simpleName).toString()
        )
    }

    /**
     * Logged String representation of Object. Each field in new line.
     *
     * @param obj a class for representation
     */
    @JvmStatic
    fun objectInfo(obj: Any) {
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, Format.objectInfo(obj), obj.javaClass.simpleName).toString()
        )
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     */
    @JvmStatic
    fun hex(data: ByteArray?, countPerLine: Int) {
        i(Format.hex(data, countPerLine).toString())
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     */
    @JvmStatic
    fun hex(data: ByteArray?) {
        i(Format.hex(data).toString())
    }

    /**
     * Logged readable representation of xml with indentation 2
     *
     * @param xmlStr your xml data
     */
    @JvmStatic
    fun xml(xmlStr: String?) {
        i(Format.xml(xmlStr).toString())
    }

    /**
     * Logged readable representation of xml
     *
     * @param xmlStr      your xml data
     * @param indentation xml identetion
     */
    @JvmStatic
    fun xml(xmlStr: String?, indentation: Int) {
        i(Format.xml(xmlStr, indentation).toString())
    }

    // =========================== Thread and stack trace ===============================
    /**
     * Logged the current Thread info
     */
    @JvmStatic
    fun threadInfo() {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, sb, message = null, title = null).toString()
        )
    }

    /**
     * Logged the current Thread info and an throwable
     *
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(throwable: Throwable) {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        addStackTrace(sb, throwable)
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, sb, message = null, title = null).toString()
        )
    }

    /**
     * Logged the current Thread info and a message
     */
    @JvmStatic
    fun threadInfo(message: String?) {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        addMessage(sb, message)
        val data = Format.getLocationContainer()
        logToAll(
            Level.INFO,
            data.tag,
            getFormattedMessage(data, sb, message = null, title = null).toString()
        )
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param message   The message you would like logged.
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(message: String?, throwable: Throwable) {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        addMessage(sb, message)
        addStackTrace(sb, throwable)
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedMessage(data, sb.toString()).toString())
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param thread    for Logged info.
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(thread: Thread?, throwable: Throwable) {
        val sb = StringBuilder()
        addThreadInfo(sb, thread)
        sb.append(Format.NL)
        addStackTrace(sb, throwable)
        val data = Format.getLocationContainer()
        logToAll(Level.INFO, data.tag, getFormattedMessage(data, sb.toString()).toString())
    }

    /**
     * Logged current stack trace.
     */
    @JvmStatic
    fun stackTrace() {
        stackTraceI("Current stack trace:")
    }

    /**
     * Logged current stack trace with a message. Level VERBOSE
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceV(message: String) {
        stackTrace(Level.VERBOSE, message)
    }

    /**
     * Logged current stack trace with a message. Level INFO
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceI(message: String) {
        stackTrace(Level.INFO, message)
    }

    /**
     * Logged current stack trace with a message. Level DEBUG
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceD(message: String) {
        stackTrace(Level.DEBUG, message)
    }

    /**
     * Logged current stack trace with a message. Level WARNING
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceW(message: String) {
        stackTrace(Level.WARNING, message)
    }

    /**
     * Logged current stack trace with a message. Level ERROR
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceE(message: String) {
        stackTrace(Level.ERROR, message)
    }

    /**
     * Logged current stack trace with a message. Level WTF
     *
     * @param message a custom message
     */
    @JvmStatic
    fun stackTraceWTF(message: String) {
        stackTrace(Level.WTF, message)
    }

    @JvmStatic
    internal fun stackTrace(level: Level, message: String) {
        val sb = StringBuilder()
        addMessage(sb, message)
        addStackTrace(sb, Thread.currentThread())
        val data = Format.getLocationContainer()
        logToAll(level, data.tag, getFormattedMessage(data, sb.toString()).toString())
    }

    // ======================== Interceptors ==============================

    private val interceptors = HashMap<Int, LogInterceptor>()
    private var standardInterceptorHashCode =0

    init {
        addLogCatInterceptor()
    }

    /**
     * Set LogCat logs enabled
     */
    @JvmStatic
    @Suppress("unused")
    fun setEnabled() {
        interceptors.forEach { 
            it.value.enabled = true
        }
    }

    /**
     * Set LogCat logs disabled
     */
    @JvmStatic
    fun setDisabled() {
        interceptors.forEach {
            it.value.enabled = false
        }
    }

    /**
     * Is LogCat logs enabled
     */
    @JvmStatic
    fun isEnabled() =  interceptors[standardInterceptorHashCode]?.enabled == true

    /**
     * Is LogCat logs disabled
     */
    @JvmStatic
    @Suppress("unused")
    fun isDisabled() = interceptors[standardInterceptorHashCode]?.enabled == false

    @JvmStatic
    fun addInterceptor(interceptor: LogInterceptor) {
        interceptors[interceptor.hashCode()] = interceptor
    }

    @JvmStatic
    @Suppress("unused")
    fun removeInterceptor(interceptor: LogInterceptor) {
        interceptors.remove(interceptor.hashCode())
    }

    @JvmStatic
    @Suppress("unused")
    fun removeStandardInterceptor() {
        interceptors.remove(standardInterceptorHashCode)
    }

    @JvmStatic
    @Suppress("unused")
    fun removeInterceptors() {
        interceptors.clear()
    }

    @JvmStatic
    @Suppress("unused")
    fun removeLogCatInterceptor() {
        interceptors.remove(standardInterceptorHashCode)
    }

    @JvmStatic
    @Suppress("unused")
    fun addLogCatInterceptor() {
        val interceptor = LogCatInterceptor()
        standardInterceptorHashCode = interceptor.hashCode()
        interceptors[standardInterceptorHashCode] = interceptor
    }

    @JvmStatic
    @Suppress("unused")
    fun getInterceptor(hashCode: Int): LogInterceptor? {
        return interceptors[hashCode]
    }

    @JvmStatic
    @Suppress("unused")
    fun hasInterceptor(hashCode: Int): Boolean {
        return interceptors.containsKey(hashCode)
    }

    @JvmStatic
    @Suppress("unused")
    fun hasInterceptor(interceptor: LogInterceptor): Boolean {
        return interceptors.containsValue(interceptor)
    }

    @JvmStatic
    internal fun logToAll(level: Level, tag: String?, message: String?) {
        for (interceptor in interceptors.values) {
            if (interceptor.enabled) {
                interceptor.log(level, tag, message, null)
            }
        }
    }

    @JvmStatic
    internal fun logToAll(level: Level, tag: String?, message: String?, throwable: Throwable?) {
        for (interceptor in interceptors.values) {
            if (interceptor.enabled) {
                interceptor.log(level, tag, message, throwable)
            }
        }
    }
}