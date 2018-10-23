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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
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
import static ua.at.tsvetkov.util.Level.DEBUG;
import static ua.at.tsvetkov.util.Level.ERROR;
import static ua.at.tsvetkov.util.Level.INFO;
import static ua.at.tsvetkov.util.Level.VERBOSE;
import static ua.at.tsvetkov.util.Level.WARNING;
import static ua.at.tsvetkov.util.Level.WTF;

/**
 * Shows a long log string in LogCat. The LogCat have the real message size for both binary and non-binary logs is ~4076 bytes.
 * The LongLog the same as the Log class, but can print to LogCat a full message - split to several usual messages.
 *
 * @author A.Tsvetkov 2018 http://tsvetkov.at.ua mailto:al@ukr.net
 */
public class LongLog extends AbstractLog {

    /**
     * Default size for message string.
     */
    public static int MAX_CHUNK = 3800;

    public static int getMaxChunk() {
        return MAX_CHUNK;
    }

    public static void setMaxChunk(int maxChunk) {
        MAX_CHUNK = maxChunk;
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param message The message you would like logged.
     */
    public static void v(String message) {
        print(getTag(), getFormattedMessage(message), VERBOSE, false);
    }

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    public static void d(String message) {
        print(getTag(), getFormattedMessage(message), DEBUG, false);
    }

    /**
     * Send a INFO log message.
     *
     * @param message The message you would like logged.
     */
    public static void i(String message) {
        print(getTag(), getFormattedMessage(message), INFO, false);
    }

    /**
     * Send a WARN log message.
     *
     * @param message The message you would like logged.
     */
    public static void w(String message) {
        print(getTag(), getFormattedMessage(message), WARNING, false);
    }

    /**
     * Send a ERROR log message.
     *
     * @param message The message you would like logged.
     */
    public static void e(String message) {
        print(getTag(), getFormattedMessage(message), ERROR, false);
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call
     * stack. Depending on system configuration, a report may be added to the DropBoxManager and/or the process may be terminated immediately
     * with an error dialog.
     *
     * @param message The message you would like logged.
     */
    public static void wtf(String message) {
        print(getTag(), getFormattedMessage(message), WTF, false);
    }

    // ==========================================================

    /**
     * Send a VERBOSE log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void v(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), VERBOSE, true);
    }

    /**
     * Send a DEBUG log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void d(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), DEBUG, true);
    }

    /**
     * Send a INFO log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void i(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), INFO, true);
    }

    /**
     * Send a WARN log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void w(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), WARNING, true);
    }

    /**
     * Send a ERROR log message and log the throwable.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void e(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), ERROR, true);
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
        print(getTag(), getFormattedThrowable(message, tr), ERROR, true);
    }

    /**
     * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
     *
     * @param message The message you would like logged.
     * @param tr      An throwable to log
     */
    public static void wtf(String message, Throwable tr) {
        print(getTag(), getFormattedThrowable(message, tr), WTF, true);
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
        print(getTag(obj), getFormattedMessage(message), VERBOSE, false);
    }

    /**
     * Send a <b>DEBUG</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void d(Object obj, String message) {
        print(getTag(obj), getFormattedMessage(message), DEBUG, false);
    }

    /**
     * Send a <b>INFO</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void i(Object obj, String message) {
        print(getTag(obj), getFormattedMessage(message), INFO, false);
    }

    /**
     * Send a <b>WARN</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
     * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
     *
     * @param obj     main class
     * @param message The message you would like logged.
     */
    public static void w(Object obj, String message) {
        print(getTag(obj), getFormattedMessage(message), WARNING, false);
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
        print(getTag(obj), getFormattedMessage(message), ERROR, false);
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
        print(getTag(obj), getFormattedMessage(message), WTF, false);
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
        print(getTag(obj), getFormattedThrowable(message, tr), VERBOSE, true);
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
        print(getTag(obj), getFormattedThrowable(message, tr), DEBUG, true);
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
        print(getTag(obj), getFormattedThrowable(message, tr), INFO, true);
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
        print(getTag(obj), getFormattedThrowable(message, tr), WARNING, true);
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
        print(getTag(obj), getFormattedThrowable(message, tr), ERROR, true);
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
        print(getTag(obj), getFormattedThrowable(message, tr), WTF, true);
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
     * @param map a Map
     */
    public static void map(Map<?, ?> map, String title) {
        print(getTag(), getFormattedMessage(Format.map(map), title), INFO, false);
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
     * @param list a List
     */
    public static void list(List<?> list, String title) {
        print(getTag(), getFormattedMessage(Format.list(list), title), INFO, false);
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
     */
    public static <T> void array(T[] array, String title) {
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
    }

    /**
     * Logged String representation of String array. Each item in new line.
     *
     * @param array an array
     */
    public static void array(String[] array, String title) {
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
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
        print(getTag(), getFormattedMessage(Format.array(array), title), INFO, false);
    }

    /**
     * Logged String representation of class.
     *
     * @param obj a class for representation
     */
    public static void objl(Object obj) {
        print(getTag(), getFormattedMessage(Format.objl(obj), obj.getClass().getSimpleName()), INFO, false);
    }

    /**
     * Logged String representation of Object. Each field in new line.
     *
     * @param obj a class for representation
     */
    public static void objn(Object obj) {
        print(getTag(), getFormattedMessage(Format.objn(obj), obj.getClass().getSimpleName()), INFO, false);
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     */
    public static void hex(byte[] data, int countPerLine) {
        print(Format.getTag(), Format.getFormattedMessage(Format.hex(data, countPerLine)), Level.INFO, false);
    }

    /**
     * Logged readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     */
    public static void hex(byte[] data) {
        print(Format.getTag(), Format.getFormattedMessage(Format.hex(data)), Level.INFO, false);
    }

    /**
     * Logged readable representation of xml with indentation 2
     *
     * @param xmlStr your xml data
     */
    public static void xml(String xmlStr) {
        print(Format.getTag(), Format.getFormattedMessage(Format.xml(xmlStr)), Level.INFO, false);
    }

    /**
     * Logged readable representation of xml
     *
     * @param xmlStr      your xml data
     * @param indentation xml identetion
     */
    public static void xml(String xmlStr, int indentation) {
        print(Format.getTag(), Format.getFormattedMessage(Format.xml(xmlStr, indentation)), Level.INFO, false);
    }

    // =========================== Thread and stack trace ===============================

    /**
     * Logged the current Thread info
     */
    public static void threadInfo() {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
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

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
    }

    /**
     * Logged the current Thread info and a message
     */
    public static void threadInfo(@Nullable String message) {
        StringBuilder sb = new StringBuilder();
        addThreadInfo(sb, currentThread());
        sb.append(NL);
        addMessage(sb, message);

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
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

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
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

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
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

        print(getTag(), getFormattedMessage(sb), VERBOSE, false);
    }

    // =========================== Private methods ===============================

    private static void print(String tag, StringBuilder sb, Level level, boolean isThrowableLog) {
        ArrayList<SoftReference<CharSequence>> list = split(sb);
        sb.setLength(0);
        int size = list.size();
        String message = null;
        for (int i = 0; i < size; i++) {
            CharSequence cs = list.get(i).get();
            if (cs != null) {
                message = cs.toString();
                if (i > 0 && i < size) {
                    if (isThrowableLog) {
                        if (!message.startsWith(Format.THROWABLE_DELIMITER_START)) {
                            message = Format.THROWABLE_DELIMITER_START + message;
                        }
                    } else {
                        if (!message.startsWith(Format.DELIMITER_START)) {
                            message = Format.DELIMITER_START + message;
                        }
                    }

                }
                String counter1 = "(Long message - part " + (i + 1) + " from " + size + ")";
                String counter = "(" + (i + 1) + " from " + size + ")\n";
                if (i == 0) {
                    if (message.endsWith("\n")) {
                        message = counter1 + message + "...";
                    } else {
                        message = counter1 + message + "\n...";
                    }
                } else if (i == size - 1) {
                    message = counter + "...\n" + message;
                } else {
                    if (message.endsWith("\n")) {
                        message = counter + "...\n" + message + "...";
                    } else {
                        message = counter + "...\n" + message + "\n...";
                    }
                }
                logToAll(level, tag, message);
            }
        }
    }

    private static ArrayList<SoftReference<CharSequence>> split(StringBuilder sb) {
        ArrayList<SoftReference<CharSequence>> list = new ArrayList<>();
        int start = 0;
        int end = MAX_CHUNK;
        int length = 0;
        while (true) {
            if (sb.length() > start + MAX_CHUNK) {
                length = sb.substring(start, end).lastIndexOf('\n');
                if (length != -1) {
                    length++;
                    CharSequence message = sb.subSequence(start, start + length);
                    list.add(new SoftReference<>(message));
                    start = start + length;
                    end = start + MAX_CHUNK;
                } else {
                    CharSequence message = sb.subSequence(start, start + MAX_CHUNK);
                    list.add(new SoftReference<>(message));
                    start = start + MAX_CHUNK;
                    end = start + MAX_CHUNK;
                }
            } else {
                CharSequence message = sb.subSequence(start, sb.length());
                list.add(new SoftReference<>(message));
                break;
            }
        }
        return list;
    }

}
