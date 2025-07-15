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
    fun printListNotDestroyedActivities() {
        Log.i(Format.list(listOf(supportFragmentLifecycleCallbacks.keys)))
    }

    /**
     * Added auto log messages for activity lifecycle.
     *
     * @param application the application instance
     */
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
                Log.w("Can't attach Fragment Logger to Activity, work only with AppCompatActivity")
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