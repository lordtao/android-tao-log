package ua.at.tsvetkov.demo;

import android.app.Application;

import ua.at.tsvetkov.util.Log;

/**
 * Created by lordtao on 06.12.2017.
 */

public class AppTaoLogDemo extends Application {

   @Override
   public void onCreate() {
      super.onCreate();

      Log.setStamp(BuildConfig.GIT_SHA);
      Log.enableComponentsChangesLogging(this);

   }

}
