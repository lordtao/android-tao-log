package ua.at.tsvetkov.demo;

import android.app.Application;

import ua.at.tsvetkov.util.logger.LogComponents;
import ua.at.tsvetkov.util.logger.Log;

/**
 * Created by lordtao on 06.12.2017.
 */

public class AppTaoLogDemo extends Application {

   @Override
   public void onCreate() {
      super.onCreate();

      if (BuildConfig.DEBUG){
         Log.setStamp(BuildConfig.GIT_SHA);
         LogComponents.enableComponentsChangesLogging(this);
      } else {
         Log.setDisabled();
      }

   }

}
