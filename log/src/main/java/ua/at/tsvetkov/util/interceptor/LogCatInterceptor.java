package ua.at.tsvetkov.util.interceptor;

import android.support.annotation.Nullable;
import android.util.Log;

public class LogCatInterceptor extends LogInterceptor {

   @Override
   public void log(Level level, String tag, String msg, @Nullable Throwable th) {
      switch (level) {
         case VERBOSE: {
            Log.v(tag, msg);
            break;
         }
         case INFO: {
            Log.i(tag, msg);
            break;
         }
         case DEBUG: {
            Log.d(tag, msg);
            break;
         }
         case WARNING: {
            Log.w(tag, msg);
            break;
         }
         case ERROR: {
            Log.e(tag, msg);
            break;
         }
         case WTF: {
            Log.wtf(tag, msg);
            break;
         }
      }
   }

}
