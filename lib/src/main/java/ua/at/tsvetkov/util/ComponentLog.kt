package ua.at.tsvetkov.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import ua.at.tsvetkov.util.Format.getActivityMethodInfo
import ua.at.tsvetkov.util.Format.getActivityTag
import ua.at.tsvetkov.util.Format.getFormattedMessage
import ua.at.tsvetkov.util.Log.w
import java.util.*

/**
 * Activity life circle and fragments stack logger
 */
object ComponentLog {

    @Volatile
    private var activityLifecycleCallback: ActivityLifecycleCallbacks? = null

    private val supportFragmentLifecycleCallbacks = HashMap<String, FragmentManager.FragmentLifecycleCallbacks>()

    /**
     * Added auto log messages for activity lifecycle and fragment stack events.
     *
     * @param application the application instance
     */
    @JvmStatic
    fun enableComponentsChangesLogging(application: Application) {
        enableActivityLifecycleLogger(application, true)
    }

    /**
     * Added auto log messages for activity lifecycle.
     *
     * @param application the application instance
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun enableActivityLifecycleLogger(application: Application) {
        enableActivityLifecycleLogger(application, false)
    }

    /**
     * Added auto log messages for activity lifecycle.
     *
     * @param application            the application instance
     * @param isAttachFragmentLogger attach fragment stack changes logger
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun enableActivityLifecycleLogger(application: Application, isAttachFragmentLogger: Boolean) {
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
                    android.util.Log.v(getActivityTag(activity), getActivityMethodInfo(activity))
                }
            }
        }
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    /**
     * Disable auto log messages for activity lifecycle and fragment stack events.
     *
     * @param application the application instance
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun disableComponentsChangesLogging(application: Application) {
        disableActivityLifecycleLogger(application)
    }

    /**
     * Disabled auto log messages for activity lifecycle.
     *
     * @param application the application instance
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun disableActivityLifecycleLogger(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    /**
     * Enabled auto log fragment stack changes.
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun enableFragmentStackChangesLogger(activity: Activity) {
        if (Log.isEnabled()) {
            val tag = activity.javaClass.simpleName
            val logger: FragmentManager.FragmentLifecycleCallbacks = FragmentLifecycleLogger()
            supportFragmentLifecycleCallbacks[activity.toString()] = logger
            if (activity is AppCompatActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(logger, true)
            } else {
                w("Can't attach FragmentLifecycleLogger to Activity, work only with AppCompatActivity")
            }
            android.util.Log.i(tag, getFormattedMessage("Fragment Lifecycle Logger attached to $tag").toString())
        }
    }

    /**
     * Disabled auto log fragment stack changes.
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun disableFragmentStackChangesLogger(activity: Activity) {
        if (Log.isEnabled()) {
            if (activity is AppCompatActivity) {
                val tag = activity.javaClass.simpleName
                val logger = supportFragmentLifecycleCallbacks[activity.toString()]
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(logger!!)
                supportFragmentLifecycleCallbacks.remove(activity.toString())
                android.util.Log.i(tag, "Fragment Lifecycle Logger detached from $tag")
            }
        }
    }
}