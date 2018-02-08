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
 * 4. This code can be modified without any special permission from author IF AND ONLY IF
 * this license agreement will remain unchanged.
 * ****************************************************************************
 */
package ua.at.tsvetkov.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Extended logger. Allows you to automatically adequately logged class, method and line call in the log. Makes it easy to write logs. For
 * example Log.v("Test") will in the log some the record: 04-04 08:29:40.336: V > SomeClass: someMethod: 286 Test
 *
 * @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:al@ukr.net
 */
public class Log {

   private static final int MAX_TAG_LENGTH = 65;
   private static final char COLON = ':';
   private static final String PREFIX_MAIN_STRING = " ▪ ";
   private static final String GROUP = "|Group:";
   private static final String PRIORITY = "|Priority:";
   private static final String ID = "|Id:";
   private static final String THREAD_NAME = "Thread Name:";
   private static final String HALF_LINE = "---------------------";
   private static final String ACTIVITY_MESSAGE = " Activity: ";
   private static final String JAVA = ".java";
   private static final String ACTIVITY_CLASS = "android.app.Activity";
   private static final String DELIMITER_START = " · ";
   private static final String DELIMITER = "···························································································";
   private static final String THROWABLE_DELIMITER_START = " ‖ ";
   private static final String THROWABLE_DELIMITER_PREFIX = "    ";
   private static final String THROWABLE_DELIMITER = "===========================================================================================";
   private static final String NL = "\n";


   private static boolean isDisabled = false;
   private static int maxTagLength = MAX_TAG_LENGTH;
   private static String stamp = null;
   private static Application.ActivityLifecycleCallbacks activityLifecycleCallback = null;

   private Log() {
   }

   /**
    * Added auto log messages for activity lifecycle.
    *
    * @param application the application instance
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void enableActivityLifecycleAutoLogger(@NonNull Application application) {
      if (application == null) {
         Log.w("Can't enable Activity auto logger, application=null");
      }
      if (isDisabled) {
         return;
      }
      if (activityLifecycleCallback == null) {
         activityLifecycleCallback = new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
               printActivityCallMethod(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
               printActivityCallMethod(activity);
            }

            private void printActivityCallMethod(Activity activity) {
               android.util.Log.i(getActivityTag(activity), getActivityMethodInfo(activity));
            }

         };
      }

      application.registerActivityLifecycleCallbacks(activityLifecycleCallback);
   }

   /**
    * Disabled auto log messages for activity lifecycle.
    *
    * @param application the application instance
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void disableActivityLifecycleAutoLogger(@NonNull Application application) {
      if (isDisabled) {
         return;
      }
      if (application == null) {
         Log.w("Can't disable Activity auto logger, application=null");
      } else {
         application.unregisterActivityLifecycleCallbacks(activityLifecycleCallback);
      }
   }

   /**
    * Is logs disabled or enabled
    *
    * @return is disabled
    */
   public static boolean isDisabled() {
      return isDisabled;
   }

   /**
    * Set logs disabled or enabled
    *
    * @param isDisabled is disabled
    */
   public static void setDisabled(boolean isDisabled) {
      Log.isDisabled = isDisabled;
   }

   /**
    * Set stamp for mark log. You can add a stamp which are awesome for binding the commits/build time to your logs among other things.
    *
    * @param stamp
    */
   public static void setStamp(String stamp) {
      Log.stamp = stamp;
   }

   /**
    * Send a VERBOSE log message.
    *
    * @param message The message you would like logged.
    */
   public static void v(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.v(getTag(), getFormattedMessage(message));
   }

   /**
    * Send a DEBUG log message.
    *
    * @param message The message you would like logged.
    */
   public static void d(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.d(getTag(), getFormattedMessage(message));
   }

   /**
    * Send a INFO log message.
    *
    * @param message The message you would like logged.
    */
   public static void i(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.i(getTag(), getFormattedMessage(message));
   }

   /**
    * Send a WARN log message.
    *
    * @param message The message you would like logged.
    */
   public static void w(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.w(getTag(), getFormattedMessage(message));
   }

   /**
    * Send a ERROR log message.
    *
    * @param message The message you would like logged.
    */
   public static void e(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.e(getTag(), getFormattedMessage(message));
   }

   /**
    * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call
    * stack. Depending on system configuration, a report may be added to the DropBoxManager and/or the process may be terminated immediately
    * with an error dialog.
    *
    * @param message The message you would like logged.
    */
   public static void wtf(String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.wtf(getTag(), getFormattedMessage(message));
   }

   // ==========================================================

   /**
    * Send a VERBOSE log message and log the throwable.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void v(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.v(getTag(), getFormattedThrowable(message, tr));
   }

   /**
    * Send a DEBUG log message and log the throwable.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void d(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.d(getTag(), getFormattedThrowable(message, tr));
   }

   /**
    * Send a INFO log message and log the throwable.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void i(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.i(getTag(), getFormattedThrowable(message, tr));
   }

   /**
    * Send a WARN log message and log the throwable.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void w(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.w(getTag(), getFormattedThrowable(message, tr));
   }

   /**
    * Send a ERROR log message and log the throwable.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void e(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.e(getTag(), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.e(getTag(), getFormattedThrowable(message, tr));
   }

   /**
    * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
    *
    * @param message The message you would like logged.
    * @param tr      An throwable to log
    */
   public static void wtf(String message, Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.wtf(getTag(), getFormattedThrowable(message, tr));
   }

   // ==========================================================

   /**
    * Send a VERBOSE log the throwable.
    *
    * @param tr An throwable to log
    */
   public static void v(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.v(getTag(), getFormattedThrowable(tr));
   }

   /**
    * Send a DEBUG log the throwable.
    *
    * @param tr An throwable to log
    */
   public static void d(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.d(getTag(), getFormattedThrowable(tr));
   }

   /**
    * Send a INFO log the throwable.
    *
    * @param tr An throwable to log
    */
   public static void i(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.i(getTag(), getFormattedThrowable(tr));
   }

   /**
    * Send a WARN log the throwable.
    *
    * @param tr An throwable to log
    */
   public static void w(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.w(getTag(), getFormattedThrowable(tr));
   }

   /**
    * Send a ERROR log the throwable.
    *
    * @param tr An throwable to log
    */
   public static void e(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.e(getTag(), getFormattedThrowable(tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.e(getTag(), getFormattedThrowable(tr));
   }

   /**
    * What a Terrible Failure: Report an throwable that should never happen. Similar to wtf(String, Throwable), with a message as well.
    *
    * @param tr An throwable to log
    */
   public static void wtf(Throwable tr) {
      if (isDisabled) {
         return;
      }
      android.util.Log.wtf(getTag(), getFormattedThrowable(tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.v(gatExtendedTag(obj), getFormattedMessage(message));
   }

   /**
    * Send a <b>DEBUG</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
    * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
    *
    * @param obj     main class
    * @param message The message you would like logged.
    */
   public static void d(Object obj, String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.d(gatExtendedTag(obj), getFormattedMessage(message));
   }

   /**
    * Send a <b>INFO</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
    * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
    *
    * @param obj     main class
    * @param message The message you would like logged.
    */
   public static void i(Object obj, String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.i(gatExtendedTag(obj), getFormattedMessage(message));
   }

   /**
    * Send a <b>WARN</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag. Usually you can use
    * "this" in "objl" parameter. As result you receive tag string "<b>(Called Main Class) LoggedClass:MethodInLoggedClass:lineNumber</b>"
    *
    * @param obj     main class
    * @param message The message you would like logged.
    */
   public static void w(Object obj, String message) {
      if (isDisabled) {
         return;
      }
      android.util.Log.w(gatExtendedTag(obj), getFormattedMessage(message));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.e(gatExtendedTag(obj), getFormattedMessage(message));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.wtf(gatExtendedTag(obj), getFormattedMessage(message));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.v(gatExtendedTag(obj), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.d(gatExtendedTag(obj), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.i(gatExtendedTag(obj), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.w(gatExtendedTag(obj), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.e(gatExtendedTag(obj), getFormattedThrowable(message, tr));
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
      if (isDisabled) {
         return;
      }
      android.util.Log.wtf(gatExtendedTag(obj), getFormattedThrowable(message, tr));
   }

   // =========================== Collections, arrays and objects ===============================

   /**
    * Logged String representation of map. Each item in new line.
    *
    * @param map a Map
    */
   public static void map(Map<?, ?> map) {
      Log.i(LogFormatter.map(map));
   }

   /**
    * Logged String representation of list. Each item in new line.
    *
    * @param list a List
    */
   public static void list(List<?> list) {
      Log.i(LogFormatter.list(list));
   }

   /**
    * Logged String representation of Objects array. Each item in new line.
    *
    * @param array an array
    */
   public static <T> void array(T[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(int[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(float[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(boolean[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(char[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(double[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of array.
    *
    * @param array an array
    */
   public static void array(long[] array) {
      Log.i(LogFormatter.array(array));
   }

   /**
    * Logged String representation of class.
    *
    * @param obj a class for representation
    */
   public static void objl(Object obj) {
      Log.i(LogFormatter.objl(obj));
   }

   /**
    * Logged String representation of Object. Each field in new line.
    *
    * @param obj a class for representation
    */
   public static void objn(Object obj) {
      Log.i(LogFormatter.objn(obj));
   }

   /**
    * Logged readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
    *
    * @param data         your bytes array data
    * @param countPerLine count byte per line
    */
   public static void hex(byte[] data, int countPerLine) {
      Log.i(LogFormatter.hex(data, countPerLine));
   }

   /**
    * Logged readable representation of bytes array data like 0F CD AD....
    *
    * @param data your bytes array data
    */
   public static void hex(byte[] data) {
      Log.i(LogFormatter.hex(data));
   }

   /**
    * Logged readable representation of xml with indentation 2
    *
    * @param xmlStr your xml data
    */
   public static void xml(String xmlStr) {
      Log.i(LogFormatter.xml(xmlStr));
   }

   /**
    * Logged readable representation of xml
    *
    * @param xmlStr      your xml data
    * @param indentation xml identetion
    */
   public static void xml(String xmlStr, int indentation) {
      Log.i(LogFormatter.xml(xmlStr, indentation));
   }


   // =========================== Thread and stack trace ===============================

   /**
    * Logged the current Thread info
    */
   public static void threadInfo() {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addThreadInfo(sb, Thread.currentThread());
      sb.append(NL);
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   /**
    * Logged the current Thread info and an throwable
    *
    * @param throwable An throwable to log
    */
   public static void threadInfo(Throwable throwable) {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addThreadInfo(sb, Thread.currentThread());
      sb.append(NL);
      addStackTrace(sb, throwable);
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   /**
    * Logged the current Thread info and a message
    */
   public static void threadInfo(@Nullable String message) {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addThreadInfo(sb, Thread.currentThread());
      sb.append(NL);
      addMessage(sb, message);
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   /**
    * Logged the current Thread info and a message and an throwable
    *
    * @param message   The message you would like logged.
    * @param throwable An throwable to log
    */
   public static void threadInfo(String message, Throwable throwable) {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addThreadInfo(sb, Thread.currentThread());
      sb.append(NL);
      addMessage(sb, message);
      addStackTrace(sb, throwable);
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   /**
    * Logged the current Thread info and a message and an throwable
    *
    * @param thread    for Logged info.
    * @param throwable An throwable to log
    */
   public static void threadInfo(Thread thread, Throwable throwable) {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addThreadInfo(sb, thread);
      sb.append(NL);
      addStackTrace(sb, throwable);
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   /**
    * Logged current stack trace.
    */
   public static void stackTrace() {
      stackTrace(null);
   }

   /**
    * Logged current stack trace with a message.
    *
    * @param message a custom message
    */
   public static void stackTrace(String message) {
      if (isDisabled) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      addMessage(sb, message);
      addStackTrace(sb, Thread.currentThread());
      android.util.Log.v(getTag(), getFormattedMessage(sb.toString()));
   }

   private static void addStackTrace(StringBuilder sb, Throwable throwable) {
      final StackTraceElement[] traces = throwable.getStackTrace();
      addStackTrace(sb, traces);
   }

   private static void addStackTrace(StringBuilder sb, Thread thread) {
      final StackTraceElement[] traces = thread.getStackTrace();
      addStackTrace(sb, traces);
   }

   @NonNull
   private static void addStackTrace(StringBuilder sb, StackTraceElement[] traces) {
      for (int i = 4; i < traces.length; i++) {
         sb.append(THROWABLE_DELIMITER_PREFIX);
         sb.append(traces[i].toString());
         sb.append('\n');
      }
   }

   /**
    * @param thread thread for Logged
    * @return filled StringBuilder for next filling
    */
   static void addThreadInfo(StringBuilder sb, Thread thread) {
      if (thread != null) {
         long id = thread.getId();
         String name = thread.getName();
         long priority = thread.getPriority();
         sb.append(THREAD_NAME);
         sb.append(name);
         sb.append(ID);
         sb.append(id);
         sb.append(PRIORITY);
         sb.append(priority);
         sb.append(GROUP);
         ThreadGroup group = thread.getThreadGroup();
         if (group != null) {
            String groupName = group.getName();
            sb.append(groupName);
         } else {
            sb.append("null");
         }
      } else {
         sb.append("The thread == null");
      }
   }

   private static void addMessage(StringBuilder sb, String message) {
      if (message != null && message.length() > 0) {
         sb.append(message);
         sb.append(NL);
      }
   }

   // ============================ Private common methods ==============================

   static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
         Class<?> superClass = clazz.getSuperclass();
         if (superClass == null) {
            throw e;
         } else {
            return getField(superClass, fieldName);
         }
      }
   }

   private static String getTag() {
      final String className = Log.class.getName();
      final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
      StringBuilder sb = new StringBuilder();
      sb.append(PREFIX_MAIN_STRING);
      addStamp(sb);
      addLocation(className, traces, sb);
      addSpaces(sb);

      return sb.toString();
   }

   private static String gatExtendedTag(Object obj) {
      if (obj == null) {
         Log.v("null");
      }
      Class clazz = obj.getClass();
      String className = clazz.getName();
      String classSimpleName = clazz.getSimpleName();
      String parentClassName = Log.class.getName();

      final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

      StringBuilder sb = new StringBuilder();
      sb.append(PREFIX_MAIN_STRING);
      addStamp(sb);

      int sbPrefixLength = sb.length();

      if (!clazz.isAnonymousClass()) {
         for (int i = 0; i < traces.length; i++) {
            if (traces[i].getClassName().startsWith(className)) {
               sb.append(classSimpleName);
               sb.append(JAVA);
               sb.append(' ');
               break;
            }
         }
      } else {
         sb.append("(Anonymous Class) ");
      }
      if (sb.length() > sbPrefixLength) {
         sb.append('<');
         sb.append('-');
         sb.append(' ');
      }
      addLocation(parentClassName, traces, sb);
      addSpaces(sb);

      return sb.toString();
   }

   private static String getActivityTag(Activity activity) {
      String className = activity.getClass().getCanonicalName();
      String classSimpleName = activity.getClass().getSimpleName();

      final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

      StringBuilder sb = new StringBuilder();
      sb.append(PREFIX_MAIN_STRING);
      addStamp(sb);

      StackTraceElement trace = findStackTraceElement(traces, className);

      if (trace != null) {
         addClassLink(sb, classSimpleName, trace.getLineNumber());
      } else {
         addClassLink(sb, classSimpleName, 0);
         trace = findStackTraceElement(traces, ACTIVITY_CLASS);
      }

      sb.append(trace.getMethodName());

      addSpaces(sb);

      return sb.toString();
   }

   private static String getActivityMethodInfo(Activity activity) {
      String className = activity.getClass().getCanonicalName();
      String classSimpleName = activity.getClass().getSimpleName();

      final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

      StringBuilder sb = new StringBuilder();
      sb.append(HALF_LINE);
      sb.append(ACTIVITY_MESSAGE);

      StackTraceElement trace = findStackTraceElement(traces, className);

      boolean isOverride = false;
      if (trace != null) {
         isOverride = true;
      } else {
         trace = findStackTraceElement(traces, ACTIVITY_CLASS);
      }

      sb.append(' ');
      sb.append(classSimpleName);

      if (isOverride) {
         sb.append(" @Override");
      } else {
         sb.append(" not override");
      }

      sb.append(" -> ");
      sb.append(trace.getMethodName());
      sb.append(' ');
      sb.append(HALF_LINE);

      return sb.toString();
   }

   private static void addStamp(StringBuilder sb) {
      if (stamp != null && stamp.length() > 0) {
         sb.append(stamp);
         sb.append(' ');
      }
   }

   private static void addLocation(String className, StackTraceElement[] traces, StringBuilder sb) {
      boolean found = false;
      for (int i = 0; i < traces.length; i++) {
         try {
            if (found) {
               if (!traces[i].getClassName().startsWith(className)) {
                  Class<?> clazz = Class.forName(traces[i].getClassName());
                  addClassLink(sb, getClassName(clazz), traces[i].getLineNumber());
                  sb.append(traces[i].getMethodName());
                  break;
               }
            } else if (traces[i].getClassName().startsWith(className)) {
               found = true;
            }
         } catch (ClassNotFoundException e) {
            android.util.Log.e("LOG", e.toString());
         }
      }
   }

   private static void addClassLink(StringBuilder sb, String className, int lineNumber) {
      sb.append('(');
      sb.append(className);
      sb.append(JAVA);
      sb.append(COLON);
      sb.append(lineNumber);
      sb.append(')');
      sb.append(' ');
   }

   private static void addSpaces(StringBuilder sb) {
      sb.append(' ');
      int extraSpaceCount = maxTagLength - sb.length();
      if (extraSpaceCount < 0) {
         maxTagLength = sb.length();
         extraSpaceCount = 0;
      }
      for (int i = 0; i < extraSpaceCount; i++) {
         sb.append(' ');
      }
      sb.append('\u21DB');
   }

   private static String getClassName(Class<?> clazz) {
      if (clazz != null) {
         if (!TextUtils.isEmpty(clazz.getSimpleName())) {
            if (clazz.getName().contains("$")) {
               return clazz.getName().substring(clazz.getName().lastIndexOf(0x2e) + 1, clazz.getName().lastIndexOf(0x24));
            } else {
               return clazz.getSimpleName();
            }
         }
         return getClassName(clazz.getEnclosingClass());
      }
      return "";
   }

   private static StackTraceElement findStackTraceElement(StackTraceElement[] traces, String startsFrom) {
      StackTraceElement trace = null;
      for (int i = 0; i < traces.length; i++) {
         if (traces[i].getClassName().startsWith(startsFrom)) {
            trace = traces[i];
            break;
         }
      }
      return trace;
   }

   private static String getFormattedMessage(String message) {
      String[] lines = message.split("\\n");
      StringBuilder sb = new StringBuilder();
      sb.append(DELIMITER);
      sb.append(NL);
      for (int i = 0; i < lines.length; i++) {
         sb.append(DELIMITER_START);
         sb.append(lines[i]);
         sb.append(NL);
      }
      sb.append(' ');
      sb.append(DELIMITER);
      return sb.toString();
   }

   private static String getFormattedThrowable(Throwable throwable) {
      return getFormattedThrowable(null, throwable);
   }

   private static String getFormattedThrowable(String message, Throwable throwable) {
      StringBuilder sb = new StringBuilder();
      sb.append(THROWABLE_DELIMITER);
      sb.append(NL);
      addFormattedMessageForTrowable(sb, message);
      sb.append(THROWABLE_DELIMITER_START);
      sb.append(throwable.toString());
      sb.append(NL);
      if (throwable == null) {
         sb.append(THROWABLE_DELIMITER_START);
         sb.append(THROWABLE_DELIMITER_PREFIX);
         sb.append("throwable == null");
         sb.append(NL);
      } else {
         StackTraceElement[] st = throwable.getStackTrace();
         for (int i = 0; i < st.length; i++) {
            sb.append(THROWABLE_DELIMITER_START);
            sb.append(THROWABLE_DELIMITER_PREFIX);
            sb.append(st[i].toString());
            sb.append(NL);
         }
      }
      sb.append(' ');
      sb.append(THROWABLE_DELIMITER);
      return sb.toString();
   }

   private static void addFormattedMessageForTrowable(StringBuilder sb, String message) {
      if (message != null) {
         String[] lines = message.split("\\n");
         for (int i = 0; i < lines.length; i++) {
            sb.append(THROWABLE_DELIMITER_START);
            sb.append(lines[i]);
            sb.append(NL);
         }
      }
   }

}
