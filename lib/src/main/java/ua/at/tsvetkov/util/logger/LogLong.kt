/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
package ua.at.tsvetkov.util.logger

import ua.at.tsvetkov.util.logger.Log.logToAll
import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.utils.Format
import ua.at.tsvetkov.util.logger.utils.Format.addMessage
import ua.at.tsvetkov.util.logger.utils.Format.addStackTrace
import ua.at.tsvetkov.util.logger.utils.Format.addThreadInfo
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedMessage
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedThrowable
import java.lang.ref.SoftReference

/**
 * Shows a long log string in LogCat. The LogCat have the real message size for both binary and non-binary logs is ~4076 bytes.
 * The LogLong the same as the Log class, but can print to LogCat a full message - split to several usual messages.
 *
 * @author A.Tsvetkov 2018 http://tsvetkov.at.ua mailto:tsvetkov2010@gmail.com
 */
object LogLong {

    /**
     * Default size for message string.
     */
    private var maxChunk = 3800

    /**
     * Send a VERBOSE log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun v(message: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, message!!), Level.VERBOSE, false)
    }

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun d(message: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, message!!), Level.DEBUG, false)
    }

    /**
     * Send a INFO log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun i(message: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, message!!), Level.INFO, false)
    }

    /**
     * Send a WARN log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun w(message: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, message!!), Level.WARNING, false)
    }

    /**
     * Send a ERROR log message.
     *
     * @param message The message you would like logged.
     */
    @JvmStatic
    fun e(message: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, message!!), Level.ERROR, false)
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
        print(data.tag, getFormattedMessage(data, message!!), Level.WTF, false)
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
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.VERBOSE, true)
    }

    /**
     * Send a DEBUG log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun d(message: String?, tr: Throwable?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.DEBUG, true)
    }

    /**
     * Send a INFO log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun i(message: String?, tr: Throwable?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.INFO, true)
    }

    /**
     * Send a WARN log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun w(message: String?, tr: Throwable?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.WARNING, true)
    }

    /**
     * Send a ERROR log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun e(message: String?, tr: Throwable?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.ERROR, true)
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
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.ERROR, true)
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    @JvmStatic
    fun wtf(message: String?, tr: Throwable?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedThrowable(data, message, tr!!), Level.WTF, true)
    }

    // =========================== Collections, arrays and objects ===============================

    /**
     * Logged String representation of map. Each item in new line.
     *
     * @param map a Map
     */
    @JvmOverloads
    @JvmStatic
    fun map(map: Map<*, *>?, title: String? = "Map") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.map(map), title), Level.INFO, false)
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    @JvmOverloads
    @JvmStatic
    fun list(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.list(list), title), Level.INFO, false)
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    @JvmOverloads
    @JvmStatic
    fun listD(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.list(list), title), Level.DEBUG, false)
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    @JvmOverloads
    @JvmStatic
    fun listV(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.list(list), title), Level.VERBOSE, false)
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    @JvmOverloads
    @JvmStatic
    fun listW(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.list(list), title), Level.WARNING, false)
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    @JvmOverloads
    @JvmStatic
    fun listE(list: List<*>?, title: String? = "List") {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.list(list), title), Level.ERROR, false)
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     */
    @JvmStatic
    fun <T : Any> array(array: Array<T>?) {
        val data = Format.getLocationContainer()
        array(array, Format.ARRAY)
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     */
    @JvmStatic
    fun <T : Any> array(array: Array<T>?, title: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.arrayT(array), title), Level.INFO, false)
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     */
    @JvmStatic
    fun array(array: Array<String>?, title: String?) {
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, Format.arrayString(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), title), Level.INFO, false)
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
        print(data.tag, getFormattedMessage(data, Format.array(array), message = null, title), Level.INFO, false)
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     */
    @JvmStatic
    fun hex(array: ByteArray?, countPerLine: Int) {
        val data = Format.getLocationContainer()
        print(
            data.tag,
            getFormattedMessage(data, Format.hex(array, countPerLine), message = null, title = null),
            Level.INFO,
            false
        )
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     */
    @JvmStatic
    fun hex(array: ByteArray?) {
        val data = Format.getLocationContainer()
        print(
            data.tag,
            getFormattedMessage(data, Format.hex(array), message = null, title = null),
            Level.INFO,
            false
        )
    }

    /**
     * Logged readable representation of xml with indentation 2
     *
     * @param xmlStr your xml data
     */
    @JvmStatic
    fun xml(xmlStr: String?) {
        val data = Format.getLocationContainer()
        print(
            data.tag,
            getFormattedMessage(data, Format.xml(xmlStr), message = null, title = null),
            Level.INFO,
            false
        )
    }

    /**
     * Logged readable representation of xml
     *
     * @param xmlStr      your xml data
     * @param indentation xml identetion
     */
    @JvmStatic
    fun xml(xmlStr: String?, indentation: Int) {
        val data = Format.getLocationContainer()
        print(
            data.tag,
            getFormattedMessage(data, Format.xml(xmlStr, indentation), message = null, title = null),
            Level.INFO,
            false
        )
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
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), Level.VERBOSE, false)
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
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), Level.VERBOSE, false)
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
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), Level.VERBOSE, false)
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
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), Level.VERBOSE, false)
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
        val data = Format.getLocationContainer()
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), Level.VERBOSE, false)
    }

    /**
     * Logged current stack trace. Level INFO
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
        print(data.tag, getFormattedMessage(data, sb, message = null, title = null), level, false)
    }

    // =========================== Private methods ===============================
    @JvmStatic
    internal fun print(tag: String, sb: StringBuilder, level: Level, isThrowableLog: Boolean) {
        val list = split(sb)
        sb.setLength(0)
        val size = list.size
        var message: String?
        for (i in 0 until size) {
            val cs = list[i].get()
            if (cs != null) {
                message = cs.toString()
                if (i in 1 until size) {
                    if (isThrowableLog) {
                        if (!message.startsWith(Format.THROWABLE_DELIMITER_START)) {
                            message = Format.THROWABLE_DELIMITER_START + message
                        }
                    } else {
                        if (!message.startsWith(Format.DELIMITER_START)) {
                            message = Format.DELIMITER_START + message
                        }
                    }
                }
                val counter1 = "[Long message - part " + (i + 1) + " from " + size + "] "
                val counter = """[${i + 1} from $size]
"""
                message = if (i == 0) {
                    if (message.endsWith("\n")) {
                        "$counter1 $message..."
                    } else {
                        "$counter1 $message\n..."
                    }
                } else if (i == size - 1) {
                    "$counter...\n$message"
                } else {
                    if (message.endsWith("\n")) {
                        "$counter...\n$message..."
                    } else {
                        "$counter...\n$message\n..."
                    }
                }
                logToAll(level, tag, message)
            }
        }
    }

    @JvmStatic
    internal fun split(sb: StringBuilder): ArrayList<SoftReference<CharSequence>> {
        val list = ArrayList<SoftReference<CharSequence>>()
        var start = 0
        var end = maxChunk
        var length: Int
        while (true) {
            if (sb.length > start + maxChunk) {
                length = sb.substring(start, end).lastIndexOf('\n')
                if (length != -1) {
                    length++
                    val message = sb.subSequence(start, start + length)
                    list.add(SoftReference(message))
                    start += length
                    end = start + maxChunk
                } else {
                    val message = sb.subSequence(start, start + maxChunk)
                    list.add(SoftReference(message))
                    start += maxChunk
                    end = start + maxChunk
                }
            } else {
                val message = sb.subSequence(start, sb.length)
                list.add(SoftReference(message))
                break
            }
        }
        return list
    }
}