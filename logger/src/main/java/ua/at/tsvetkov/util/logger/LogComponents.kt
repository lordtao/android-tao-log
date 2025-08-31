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

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import ua.at.tsvetkov.util.logger.utils.Format
import ua.at.tsvetkov.util.logger.utils.FragmentLifecycleLogger

/**
 * Activity life circle and fragments stack logger
 */
object LogComponents {

    @Volatile
    private var activityLifecycleCallback: ActivityLifecycleCallbacks? = null

    private val supportFragmentLifecycleCallbacks =
        HashMap<String, FragmentManager.FragmentLifecycleCallbacks>()

    /**
     * Added auto log messages for activity lifecycle and fragment stack events.
     *
     * @param application the application instance
     */
    @JvmStatic
    fun enableComponentsChangesLogging(application: Application) {
        enableActivityLifecycleLogger(application, true)
    }

    @JvmStatic
    @Suppress("unused")
    fun printListNotDestroyedActivities() {
        Log.i(Format.list(listOf(supportFragmentLifecycleCallbacks.keys)))
    }

    /**
     * Added auto log messages for activity lifecycle.
     *
     * @param application the application instance
     */
    @Suppress("unused")
    fun enableActivityLifecycleLogger(application: Application) {
        enableActivityLifecycleLogger(application, false)
    }

    /**
     * Added auto log messages for activity lifecycle.
     *
     * @param application            the application instance
     * @param isAttachFragmentLogger attach fragment stack changes logger
     */
    private fun enableActivityLifecycleLogger(
        application: Application,
        isAttachFragmentLogger: Boolean
    ) {
        if (activityLifecycleCallback == null) {
            activityLifecycleCallback = object : ActivityLifecycleCallbacks {

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    printActivityCallMethod(activity)
                    if (isAttachFragmentLogger) {
                        enableFragmentStackChangesLogger(activity)
                    }
                }

                override fun onActivityStarted(activity: Activity) {
                    printActivityCallMethod(activity)
                }

                override fun onActivityResumed(activity: Activity) {
                    printActivityCallMethod(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                    printActivityCallMethod(activity)
                }

                override fun onActivityStopped(activity: Activity) {
                    printActivityCallMethod(activity)
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    printActivityCallMethod(activity)
                }

                override fun onActivityDestroyed(activity: Activity) {
                    printActivityCallMethod(activity)
                    if (isAttachFragmentLogger) {
                        disableFragmentStackChangesLogger(activity)
                    }
                }

                private fun printActivityCallMethod(activity: Activity) {
                    android.util.Log.v("ACTIVITY >>> ", getActivityMethodInfo(activity))
                }
            }
        }
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    private fun getActivityMethodInfo(activity: Activity): String {
        val className = activity.javaClass.canonicalName!!
        val classSimpleName = activity.javaClass.simpleName

        val traces = Thread.currentThread().stackTrace

        val sb = StringBuilder()

        var trace = Format.findStackTraceElement(traces, className)

        if (trace == null) {
            trace = Format.findStackTraceElement(traces, Format.ACTIVITY_CLASS)
        }

        sb.append(Format.HALF_LINE)
        sb.append(Format.SPACE)
        sb.append(classSimpleName)
        sb.append(" -> ")
        sb.append(trace!!.methodName)
        sb.append(Format.SPACE)
        sb.append(Format.HALF_LINE)

        return sb.toString()
    }

    /**
     * Disable auto log messages for activity lifecycle and fragment stack events.
     *
     * @param application the application instance
     */
    @Suppress("unused")
    fun disableComponentsChangesLogging(application: Application) {
        disableActivityLifecycleLogger(application)
    }

    /**
     * Disabled auto log messages for activity lifecycle.
     *
     * @param application the application instance
     */
    fun disableActivityLifecycleLogger(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    /**
     * Enabled auto log fragment stack changes.
     *
     * @param activity
     */
    fun enableFragmentStackChangesLogger(activity: Activity) {
        if (Log.isEnabled()) {
            val tag = activity.javaClass.simpleName
            val logger: FragmentManager.FragmentLifecycleCallbacks = FragmentLifecycleLogger()
            supportFragmentLifecycleCallbacks[activity.toString()] = logger
            if (activity is AppCompatActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(logger, true)
            } else {
                Log.w("Can't attach Fragment Logger to $tag, it works only with AppCompatActivity")
            }
        }
    }

    /**
     * Disabled auto log fragment stack changes.
     *
     * @param activity
     */
    fun disableFragmentStackChangesLogger(activity: Activity) {
        if (Log.isEnabled()) {
            if (activity is AppCompatActivity) {
                val tag = activity.javaClass.simpleName
                val logger = supportFragmentLifecycleCallbacks[activity.toString()]
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(logger!!)
                supportFragmentLifecycleCallbacks.remove(activity.toString())
                Log.i(" =====----- \uD83D\uDCA5 $tag was destroyed \uD83D\uDCA5 -----===== ")
            }
        }
    }
}