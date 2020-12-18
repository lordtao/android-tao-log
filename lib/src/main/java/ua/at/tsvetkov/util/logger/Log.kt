/******************************************************************************
 * Copyright (c) 2010 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 *
 * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 *
 *
 * Project:
 * TAO Core
 *
 *
 * License agreement:
 *
 *
 * 1. This code is published AS IS. Author is not responsible for any damage that can be
 * caused by any application that uses this code.
 * 2. Author does not give a garantee, that this code is error free.
 * 3. This code can be used in NON-COMMERCIAL applications AS IS without any special
 * permission from author.
 * 4. This code can be modified without any special permission from author IF AND OFormat.NLY IF
 * this license agreement will remain unchanged.
 */
package ua.at.tsvetkov.util.logger

import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.interceptor.LogCatInterceptor
import ua.at.tsvetkov.util.logger.interceptor.LogInterceptor
import ua.at.tsvetkov.util.logger.utils.Format
import ua.at.tsvetkov.util.logger.utils.Format.addMessage
import ua.at.tsvetkov.util.logger.utils.Format.addStackTrace
import ua.at.tsvetkov.util.logger.utils.Format.addThreadInfo
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedMessage
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedThrowable
import ua.at.tsvetkov.util.logger.utils.Format.getTag
import java.util.*

/**
 * Extended logger. Allows you to automatically adequately logged class, method and line call in the log. Makes it easy to write logs. For
 * example Log.v("Message") will in the log some the record:
 * <p>
 * 04-04 08:29:40.336: V > SomeClass: someMethod: 286  Message
 *
 * @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:tsvetkov2010@gmail.com
 */
object Log {

    /**
     * Is log have the line boundaries.
     */
    @Volatile
    @JvmStatic
    var isLogOutlined = true

    /**
     * Is print a log string in new lines with spaces (as in AndroidStudio before 3.1). False by default
     */
    @Volatile
    @JvmStatic
    var isAlignNewLines = false

    /**
     * Set stamp for mark log. You can add a stamp which are awesome for binding the commits/build time to your logs among other things.
     *
     * @param stamp
     */
    @JvmStatic
    fun setStamp(stamp: String?) {
        Format.stamp = stamp
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun v(message: String?) {
        logToAll(Level.VERBOSE, getTag(), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun d(message: String?) {
        logToAll(Level.DEBUG, getTag(), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a INFO log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun i(message: String?) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a WARN log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun w(message: String?) {
        logToAll(Level.WARNING, getTag(), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a ERROR log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun e(message: String?) {
        logToAll(Level.ERROR, getTag(), getFormattedMessage(message!!).toString())
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
        logToAll(Level.WTF, getTag(), getFormattedMessage(message!!).toString())
    }
    // ==========================================================
    /**
     * Send a VERBOSE log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun v(message: String?, tr: Throwable?) {
        logToAll(Level.VERBOSE, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a DEBUG log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun d(message: String?, tr: Throwable?) {
        logToAll(Level.DEBUG, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a INFO log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun i(message: String?, tr: Throwable?) {
        logToAll(Level.INFO, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a WARN log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun w(message: String?, tr: Throwable?) {
        logToAll(Level.WARNING, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a ERROR log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun e(message: String?, tr: Throwable?) {
        logToAll(Level.ERROR, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a ERROR log message and log the throwable. RuntimeException is not handled.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun rt(message: String?, tr: Throwable?) {
        if (tr is RuntimeException) {
            throw (tr as RuntimeException?)!!
        }
        logToAll(Level.ERROR, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun wtf(message: String?, tr: Throwable?) {
        logToAll(Level.WTF, getTag(), getFormattedThrowable(message, tr!!).toString(), tr)
    }
    // ==========================================================
    /**
     * Send a VERBOSE log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun v(tr: Throwable?) {
        logToAll(Level.VERBOSE, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * Send a DEBUG log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun d(tr: Throwable?) {
        logToAll(Level.DEBUG, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * Send a INFO log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun i(tr: Throwable?) {
        logToAll(Level.INFO, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * Send a WARN log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun w(tr: Throwable?) {
        logToAll(Level.WARNING, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * Send a ERROR log the throwable.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun e(tr: Throwable?) {
        logToAll(Level.ERROR, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * Send a ERROR log the throwable. RuntimeException is not handled.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun rt(tr: Throwable?) {
        if (tr is RuntimeException) {
            throw (tr as RuntimeException?)!!
        }
        logToAll(Level.ERROR, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param tr An throwable to log
     */
    @JvmStatic
    fun wtf(tr: Throwable?) {
        logToAll(Level.WTF, getTag(), getFormattedThrowable(tr!!).toString(), tr)
    }
    // ==========================================================
    /**
     * Send a **VERBOSE** log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumberClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun v(obj: Any?, message: String?) {
        logToAll(Level.VERBOSE, getTag(obj), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a **DEBUG** log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun d(obj: Any?, message: String?) {
        logToAll(Level.DEBUG, getTag(obj), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a **INFO** log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun i(obj: Any?, message: String?) {
        logToAll(Level.INFO, getTag(obj), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a **WARN** log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun w(obj: Any?, message: String?) {
        logToAll(Level.WARNING, getTag(obj), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a **ERROR** log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun e(obj: Any?, message: String?) {
        logToAll(Level.ERROR, getTag(obj), getFormattedMessage(message!!).toString())
    }

    /**
     * Send a **What a Terrible Failure: Report a condition that should never happen** log message. Using when you extend any Class and
     * wont to receive full info in LogCat tag. Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun wtf(obj: Any?, message: String?) {
        logToAll(Level.WTF, getTag(obj), getFormattedMessage(message!!).toString())
    }
    // ==========================================================
    /**
     * Send a **VERBOSE** log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun v(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.VERBOSE, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a **DEBUG** log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun d(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.DEBUG, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a **INFO** log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun i(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.INFO, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a **WARN** log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun w(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.WARNING, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a **ERROR** log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param tr      An throwable to log
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun e(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.ERROR, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
    }

    /**
     * Send a **What a Terrible Failure: Report a condition that should never happen** log message and log the throwable. Using when you
     * extend any Class and wont to receive full info in LogCat tag. Usually you can use "this" in "objl" parameter. As result you receive tag
     * string "**(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber**"
     *
     * @param obj     main class
     * @param tr      An throwable to log
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun wtf(obj: Any?, message: String?, tr: Throwable?) {
        logToAll(Level.WTF, getTag(obj), getFormattedThrowable(message, tr!!).toString(), tr)
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
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.map(map), title!!).toString())
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
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.list(list), title!!).toString())
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
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.arrayT(array), title).toString())
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     * @param title a title string
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: Array<String>?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.arrayString(array), title).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: IntArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: FloatArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: BooleanArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: CharArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: DoubleArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    @JvmOverloads
    @JvmStatic
    fun array(array: LongArray?, title: String? = Format.ARRAY) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.array(array), title!!).toString())
    }

    /**
     * Logged String representation of class.
     *
     * @param obj a class for representation
     */
    @JvmStatic
    fun classInfo(obj: Any) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.classInfo(obj), obj.javaClass.simpleName).toString())
    }

    /**
     * Logged String representation of Object. Each field in new line.
     *
     * @param obj a class for representation
     */
    @JvmStatic
    fun objectInfo(obj: Any) {
        logToAll(Level.INFO, getTag(), getFormattedMessage(Format.objectInfo(obj), obj.javaClass.simpleName).toString())
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
        logToAll(Level.INFO, getTag(), getFormattedMessage(sb).toString())
    }

    /**
     * Logged the current Thread info and an throwable
     *
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(throwable: Throwable?) {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        addStackTrace(sb, throwable!!)
        logToAll(Level.INFO, getTag(), getFormattedMessage(sb).toString())
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
        logToAll(Level.INFO, getTag(), getFormattedMessage(sb).toString())
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param message   The message you would like logged.
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(message: String?, throwable: Throwable?) {
        val sb = StringBuilder()
        addThreadInfo(sb, Thread.currentThread())
        sb.append(Format.NL)
        addMessage(sb, message)
        addStackTrace(sb, throwable!!)
        logToAll(Level.INFO, getTag(), getFormattedMessage(sb.toString()).toString())
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param thread    for Logged info.
     * @param throwable An throwable to log
     */
    @JvmStatic
    fun threadInfo(thread: Thread?, throwable: Throwable?) {
        val sb = StringBuilder()
        addThreadInfo(sb, thread)
        sb.append(Format.NL)
        addStackTrace(sb, throwable!!)
        logToAll(Level.INFO, getTag(), getFormattedMessage(sb.toString()).toString())
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
        logToAll(level, getTag(), getFormattedMessage(sb.toString()).toString())
    }

    // ======================== Interceptors ==============================

    private val interceptors = HashMap<Int, LogInterceptor>()
    private val logCatInterceptor = LogCatInterceptor()

    init {
        interceptors[logCatInterceptor.hashCode()] = logCatInterceptor
    }

    /**
     * Set LogCat logs enabled
     */
    @JvmStatic
    fun setEnabled() {
        logCatInterceptor.enabled = true
    }

    /**
     * Set LogCat logs disabled
     */
    @JvmStatic
    fun setDisabled() {
        logCatInterceptor.enabled = false
    }

    /**
     * Is LogCat logs enabled
     */
    @JvmStatic
    fun isEnabled() = logCatInterceptor.enabled

    /**
     * Is LogCat logs disabled
     */
    @JvmStatic
    fun isDisabled() = !logCatInterceptor.enabled

    @JvmStatic
    fun addInterceptor(interceptor: LogInterceptor) {
        interceptors.put(interceptor.hashCode(), interceptor)
    }

    @JvmStatic
    fun removeInterceptor(interceptor: LogInterceptor) {
        interceptors.remove(interceptor.hashCode())
    }

    @JvmStatic
    fun removeInterceptors() {
        interceptors.clear()
    }

    @JvmStatic
    fun removeLogCatInterceptor() {
        interceptors.remove(logCatInterceptor.hashCode())
    }

    @JvmStatic
    fun addLogCatInterceptor() {
        interceptors.put(logCatInterceptor.hashCode(), logCatInterceptor)
    }

    @JvmStatic
    fun getInterceptor(id: Int): LogInterceptor? {
        return interceptors[id]
    }

    @JvmStatic
    fun hasInterceptor(id: Int): Boolean {
        return interceptors.containsKey(id)
    }

    @JvmStatic
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