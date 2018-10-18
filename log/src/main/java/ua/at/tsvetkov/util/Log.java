/**
 * ****************************************************************************
 * Copyright (c) 2010 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p/>
 * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 * <p/>
 * Project:
 * TAO Core
 * <p/>
 * License agreement:
 * <p/>
 * 1. This code is published AS IS. Author is not responsible for any damage that can be
 * caused by any application that uses this code.
 * 2. Author does not give a garantee, that this code is error free.
 * 3. This code can be used in NON-COMMERCIAL applications AS IS without any special
 * permission from author.
 * 4. This code can be modified without any special permission from author IF AND OFormat.NLY IF
 * this license agreement will remain unchanged.
 * ****************************************************************************
 */
package ua.at.tsvetkov.util;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static ua.at.tsvetkov.util.Format.NL;
import static ua.at.tsvetkov.util.Format.addMessage;
import static ua.at.tsvetkov.util.Format.addStackTrace;
import static ua.at.tsvetkov.util.Format.addThreadInfo;
import static ua.at.tsvetkov.util.Format.getFormattedMessage;
import static ua.at.tsvetkov.util.Format.getFormattedThrowable;
import static ua.at.tsvetkov.util.Format.getTag;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.DEBUG;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.ERROR;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.INFO;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.VERBOSE;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.WARNING;
import static ua.at.tsvetkov.util.interceptor.LogInterceptor.Level.WTF;

/**
 * Extended logger. Allows you to automatically adequately logged class, method and line call in the log. Makes it easy to write logs. For
 * example Log.v("Boo") will in the log some the record: 04-04 08:29:40.336: V > SomeClass: someMethod: 286 Boo
 *
 * @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:al@ukr.net
 */
public class Log extends AbstractLog {

    static volatile boolean isLogOutlined = true;
    static volatile boolean isAlignNewLines = false;

    /**
     * Is print a log string in new lines with spaces (as in AndroidStudio before 3.1). False by default
     *
     * @return
     */
    public static boolean isAlignNewLines() {
        return isAlignNewLines;
    }

    /**
     * Set to print a log string  in new lines with spaces (as in AndroidStudio before 3.1). False by default
     *
     * @param isArrangeNewLines
     */
    public static void setAlignNewLines(boolean isArrangeNewLines) {
        Log.isAlignNewLines = isArrangeNewLines;
    }

    /**
     * Create the line boundaries of the log. True by default
     *
     * @param isLogOutlined
     */
    public static void setLogOutlined(boolean isLogOutlined) {
        Log.isLogOutlined = isLogOutlined;
    }

    /**
     * Set stamp for mark log. You can add a stamp which are awesome for binding the commits/build time to your logs among other things.
     *
     * @param stamp
     */
    public static void setStamp(String stamp) {
        Format.stamp = stamp;
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param message The message you would like logged.
     */
    public static void v(String message) {
        logToAll(VERBOSE, getTag(), getFormattedMessage(message).toString());
    }

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    public static void d(String message) {
        logToAll(DEBUG, getTag(), getFormattedMessage(message).toString());
    }

    /**
     * Send a INFO log message.
     *
     * @param message The message you would like logged.
     */
    public static void i(String message) {
        logToAll(INFO, getTag(), getFormattedMessage(message).toString());
    }

    /**
     * Send a WARN log message.
     *
     * @param message The message you would like logged.
     */
    public static void w(String message) {
        logToAll(WARNING, getTag(), getFormattedMessage(message).toString());
    }

    /**
     * Send a ERROR log message.
     *
     * @param message The message you would like logged.
     */
    public static void e(String message) {
        logToAll(ERROR, getTag(), getFormattedMessage(message).toString());
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call
     * stack. Depending on system configuration, a report may be added to the DropBoxManager and/or the process may be terminated immediately
     * with an error dialog.
     *
     * @param message The message you would like logged.
     */
    public static void wtf(String message) {
        logToAll(WTF, getTag(), getFormattedMessage(message).toString());
    }

    // ==========================================================

    /**
     * Send a VERBOSE log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void v(String message, Throwable tr) {
        logToAll(VERBOSE, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a DEBUG log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void d(String message, Throwable tr) {
        logToAll(DEBUG, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a INFO log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void i(String message, Throwable tr) {
        logToAll(INFO, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a WARN log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void w(String message, Throwable tr) {
        logToAll(WARNING, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a ERROR log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void e(String message, Throwable tr) {
        logToAll(ERROR, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a ERROR log message and log the throwable. RuntimeException is not handled.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void rt(String message, Throwable tr) {
        if (tr instanceof RuntimeException) {
            throw (RuntimeException) tr;
        }
        logToAll(ERROR, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void wtf(String message, Throwable tr) {
        logToAll(WTF, getTag(), getFormattedThrowable(message, tr).toString(), tr);
    }

    // ==========================================================

    /**
     * Send a VERBOSE log the throwable.
     *
     * @param tr An throwable to log
     */
    public static void v(Throwable tr) {
        logToAll(VERBOSE, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * Send a DEBUG log the throwable.
     *
     * @param tr An throwable to log
     */
    public static void d(Throwable tr) {
        logToAll(DEBUG, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * Send a INFO log the throwable.
     *
     * @param tr An throwable to log
     */
    public static void i(Throwable tr) {
        logToAll(INFO, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * Send a WARN log the throwable.
     *
     * @param tr An throwable to log
     */
    public static void w(Throwable tr) {
        logToAll(WARNING, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * Send a ERROR log the throwable.
     *
     * @param tr An throwable to log
     */
    public static void e(Throwable tr) {
        logToAll(ERROR, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * Send a ERROR log the throwable. RuntimeException is not handled.
     *
     * @param tr An throwable to log
     */
    public static void rt(Throwable tr) {
        if (tr instanceof RuntimeException) {
            throw (RuntimeException) tr;
        }
        logToAll(ERROR, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param tr An throwable to log
     */
    public static void wtf(Throwable tr) {
        logToAll(WTF, getTag(), getFormattedThrowable(tr).toString(), tr);
    }

    // ==========================================================

    /**
     * Send a <b>VERBOSE</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumberClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void v(Object obj, String message) {
        logToAll(VERBOSE, getTag(obj), getFormattedMessage(message).toString());
    }

    /**
     * Send a <b>DEBUG</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void d(Object obj, String message) {
        logToAll(DEBUG, getTag(obj), getFormattedMessage(message).toString());
    }

    /**
     * Send a <b>INFO</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void i(Object obj, String message) {
        logToAll(INFO, getTag(obj), getFormattedMessage(message).toString());
    }

    /**
     * Send a <b>WARN</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void w(Object obj, String message) {
        logToAll(WARNING, getTag(obj), getFormattedMessage(message).toString());
    }

    /**
     * Send a <b>ERROR</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void e(Object obj, String message) {
        logToAll(ERROR, getTag(obj), getFormattedMessage(message).toString());
    }

    /**
     * Send a <b>What a Terrible Failure: Report a condition that should never happen</b> log message. Using when you extend any Class and
     * wont to receive full info in LogCat tag. Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void wtf(Object obj, String message) {
        logToAll(WTF, getTag(obj), getFormattedMessage(message).toString());
    }

    // ==========================================================

    /**
     * Send a <b>VERBOSE</b> log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void v(Object obj, String message, Throwable tr) {
        logToAll(VERBOSE, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a <b>DEBUG</b> log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void d(Object obj, String message, Throwable tr) {
        logToAll(DEBUG, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a <b>INFO</b> log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void i(Object obj, String message, Throwable tr) {
        logToAll(INFO, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a <b>WARN</b> log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void w(Object obj, String message, Throwable tr) {
        logToAll(WARNING, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a <b>ERROR</b> log message and log the throwable. Using when you extend any Class and wont to receive full info in LogCat tag.
     * Usually you can use "this" in "objl" parameter. As result you receive tag string
     * "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param tr      An throwable to log
     * @param message The message you would like logged.
     */
    public static void e(Object obj, String message, Throwable tr) {
        logToAll(ERROR, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    /**
     * Send a <b>What a Terrible Failure: Report a condition that should never happen</b> log message and log the throwable. Using when you
     * extend any Class and wont to receive full info in LogCat tag. Usually you can use "this" in "objl" parameter. As result you receive tag
     * string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param tr      An throwable to log
     * @param message The message you would like logged.
     */
    public static void wtf(Object obj, String message, Throwable tr) {
        logToAll(WTF, getTag(obj), getFormattedThrowable(message, tr).toString(), tr);
    }

    // =========================== Collections, arrays and objects ===============================

    /**
     * Logged String representation of map. Each item in new line.
     *
     * @param map a Map
     */
    public static void map(Map<?, ?> map) {
        map(map, "Map");
    }

    /**
     * Logged String representation of map. Each item in new line.
     *
     * @param map   a Map
     * @param title a title string
     */
    public static void map(Map<?, ?> map, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.map(map), title).toString());
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list a List
     */
    public static void list(List<?> list) {
        list(list, "List");
    }

    /**
     * Logged String representation of list. Each item in new line.
     *
     * @param list  a List
     * @param title a title string
     */
    public static void list(List<?> list, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.list(list), title).toString());
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     */
    public static <T> void array(T[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of Objects array. Each item in new line.
     *
     * @param array an array
     * @param title a title string
     */
    public static <T> void array(T[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     */
    public static void array(String[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     * @param title a title string
     */
    public static void array(String[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(int[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(int[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(float[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(float[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(boolean[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(boolean[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(char[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(char[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(double[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(double[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(long[] array) {
        array(array, Format.ARRAY);
    }

    /**
     * Logged String representation of array.
     *
     * @param array an array
     */
    public static void array(long[] array, String title) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.array(array), title).toString());
    }

    /**
     * Logged String representation of class.
     *
     * @param obj a class for representation
     */
    public static void objl(Object obj) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.objl(obj), obj.getClass().getSimpleName()).toString());
    }

    /**
     * Logged String representation of Object. Each field in new line.
     *
     * @param obj a class for representation
     */
    public static void objn(Object obj) {
        logToAll(INFO, getTag(), getFormattedMessage(Format.objl(obj), obj.getClass().getSimpleName()).toString());
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     */
    public static void hex(byte[] data, int countPerLine) {
        i(Format.hex(data, countPerLine).toString());
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     */
    public static void hex(byte[] data) {
        i(Format.hex(data).toString());
    }

    /**
     * Logged readable representation of xml with indentation 2
     *
     * @param xmlStr your xml data
     */
    public static void xml(String xmlStr) {
        i(Format.xml(xmlStr).toString());
    }

    /**
     * Logged readable representation of xml
     *
     * @param xmlStr      your xml data
     * @param indentation xml identetion
     */
    public static void xml(String xmlStr, int indentation) {
        i(Format.xml(xmlStr, indentation).toString());
    }


    // =========================== Thread and stack trace ===============================

    /**
     * Logged the current Thread info
     */
    public static void threadInfo() {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);
        logToAll(INFO, getTag(), getFormattedMessage(sb).toString());
    }

    /**
     * Logged the current Thread info and an throwable
     *
     * @param throwable An throwable to log
     */
    public static void threadInfo(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);
        addStackTrace(sb, throwable);
        logToAll(INFO, getTag(), getFormattedMessage(sb).toString());
    }

    /**
     * Logged the current Thread info and a message
     */
    public static void threadInfo(@Nullable String message) {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);
        addMessage(sb, message);
        logToAll(INFO, getTag(), getFormattedMessage(sb).toString());
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param message   The message you would like logged.
     * @param throwable An throwable to log
     */
    public static void threadInfo(String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);
        addMessage(sb, message);
        addStackTrace(sb, throwable);
        logToAll(INFO, getTag(), getFormattedMessage(sb.toString()).toString());
    }

    /**
     * Logged the current Thread info and a message and an throwable
     *
     * @param thread    for Logged info.
     * @param throwable An throwable to log
     */
    public static void threadInfo(Thread thread, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, thread);
        sb.append(NL);
        addStackTrace(sb, throwable);
        logToAll(INFO, getTag(), getFormattedMessage(sb.toString()).toString());
    }

    /**
     * Logged current stack trace.
     */
    public static void stackTrace() {
        stackTrace("Current stack trace:");
    }

    /**
     * Logged current stack trace with a message.
     *
     * @param message a custom message
     */
    public static void stackTrace(String message) {
        StringBuilder sb = new StringBuilder();
        addMessage(sb, message);
        addStackTrace(sb, currentThread());
        logToAll(INFO, getTag(), getFormattedMessage(sb.toString()).toString());
    }

}
