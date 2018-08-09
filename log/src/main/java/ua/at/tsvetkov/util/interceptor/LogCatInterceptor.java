package ua.at.tsvetkov.util.interceptor;

import android.support.annotation.Nullable;

public class LogCatInterceptor extends LogInterceptor {

    @Override
    public void log(Level level, String tag, String msg, @Nullable Throwable th) {
        switch (level) {
            case VERBOSE: {
                if (th == null) {
                    android.util.Log.v(tag, msg);
                } else {
                    android.util.Log.v(tag, msg, th);
                }
                break;
            }
            case INFO: {
                if (th == null) {
                    android.util.Log.i(tag, msg);
                } else {
                    android.util.Log.i(tag, msg, th);
                }
                break;
            }
            case DEBUG: {
                if (th == null) {
                    android.util.Log.d(tag, msg);
                } else {
                    android.util.Log.d(tag, msg, th);
                }
                break;
            }
            case WARNING: {
                if (th == null) {
                    android.util.Log.w(tag, msg);
                } else {
                    android.util.Log.w(tag, msg, th);
                }
                break;
            }
            case ERROR: {
                if (th == null) {
                    android.util.Log.e(tag, msg);
                } else {
                    android.util.Log.e(tag, msg, th);
                }
                break;
            }
            case WTF: {
                if (th == null) {
                    android.util.Log.wtf(tag, msg);
                } else {
                    android.util.Log.wtf(tag, msg, th);
                }
                break;
            }
        }
    }

}
