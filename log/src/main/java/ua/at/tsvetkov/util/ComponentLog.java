package ua.at.tsvetkov.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import static android.app.Application.ActivityLifecycleCallbacks;
import static android.util.Log.v;
import static ua.at.tsvetkov.util.Format.getActivityMethodInfo;
import static ua.at.tsvetkov.util.Format.getActivityTag;
import static ua.at.tsvetkov.util.Log.w;

/**
 * Activity lifecicle and fragments stack logger
 */
public class ComponentLog {

   private static volatile Application.ActivityLifecycleCallbacks activityLifecycleCallback = null;
   private static volatile HashMap<String, FragmentManager.FragmentLifecycleCallbacks> supportFragmentLifecycleCallbacks = new HashMap<>();

   private ComponentLog() {
   }

   /**
    * Added auto log messages for activity lifecycle and fragment stack events.
    *
    * @param application the application instance
    */
   public static void enableComponentsChangesLogging(@NonNull Application application) {
      enableActivityLifecycleLogger(application, true);
   }

   /**
    * Added auto log messages for activity lifecycle.
    *
    * @param application the application instance
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void enableActivityLifecycleLogger(@NonNull Application application) {
      enableActivityLifecycleLogger(application, false);
   }

   /**
    * Added auto log messages for activity lifecycle.
    *
    * @param application            the application instance
    * @param isAttachFragmentLogger attach fragment stack changes logger
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   private static void enableActivityLifecycleLogger(@NonNull Application application, final boolean isAttachFragmentLogger) {
       if (application == null) {
           w("Can't enable Activity auto logger, application == null");
           return;
       }
       if (activityLifecycleCallback == null) {
           activityLifecycleCallback = new ActivityLifecycleCallbacks() {

               @Override
               public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                   printActivityCallMethod(activity);
                   if (isAttachFragmentLogger) {
                       enableFragmentStackChangesLogger(activity);
                   }
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
                   if (isAttachFragmentLogger) {
                       disableFragmentStackChangesLogger(activity);
                   }
               }

               private void printActivityCallMethod(Activity activity) {
                   v(getActivityTag(activity), getActivityMethodInfo(activity));
               }

           };
       }

       application.registerActivityLifecycleCallbacks(activityLifecycleCallback);
   }

   /**
    * Disable auto log messages for activity lifecycle and fragment stack events.
    *
    * @param application the application instance
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void disableComponentsChangesLogging(@NonNull Application application) {
      disableActivityLifecycleLogger(application);
   }

   /**
    * Disabled auto log messages for activity lifecycle.
    *
    * @param application the application instance
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void disableActivityLifecycleLogger(@NonNull Application application) {
       if (application == null) {
           w("Can't disable Activity auto logger, application=null");
       } else {
           application.unregisterActivityLifecycleCallbacks(activityLifecycleCallback);
       }
   }

   /**
    * Enabled auto log fragment stack changes.
    *
    * @param activity
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void enableFragmentStackChangesLogger(@NonNull Activity activity) {
      if (!Log.isDisabled()) {
         String tag = activity.getClass().getSimpleName();
         FragmentManager.FragmentLifecycleCallbacks logger = new FragmentLifecycleLogger();
         supportFragmentLifecycleCallbacks.put(activity.toString(), logger);
         if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(logger, true);
         } else {
            Log.w("Can't attach FragmentLifecycleLogger to Activity, work only with AppCompatActivity");
         }
         android.util.Log.i(tag, Format.getFormattedMessage("Fragment Lifecycle Logger attached to " + tag).toString());
      }
   }

   /**
    * Disabled auto log fragment stack changes.
    *
    * @param activity
    */
   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public static void disableFragmentStackChangesLogger(@NonNull Activity activity) {
      if (!Log.isDisabled()) {
         if (activity instanceof AppCompatActivity) {
            String tag = activity.getClass().getSimpleName();
            FragmentManager.FragmentLifecycleCallbacks logger = supportFragmentLifecycleCallbacks.get(activity.toString());
            ((AppCompatActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(logger);
            supportFragmentLifecycleCallbacks.remove(activity.toString());
            android.util.Log.i(tag, "Fragment Lifecycle Logger detached from " + tag);
         }
      }
   }

}
