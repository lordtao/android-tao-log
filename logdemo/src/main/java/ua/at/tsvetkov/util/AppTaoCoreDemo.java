package ua.at.tsvetkov.util;

import android.app.Application;

/**
 * Created by lordtao on 06.12.2017.
 */

public class AppTaoCoreDemo extends Application {

   @Override
   public void onCreate() {
      super.onCreate();

      Log.setStamp(BuildConfig.GIT_SHA);
      Log.enableActivityLifecycleAutoLogger(this);

   }

}
