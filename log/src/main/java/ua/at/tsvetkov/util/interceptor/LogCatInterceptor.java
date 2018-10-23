package ua.at.tsvetkov.util.interceptor;

import android.support.annotation.Nullable;
import android.util.Log;

import ua.at.tsvetkov.util.Level;

public class LogCatInterceptor extends LogInterceptor {

    @Override
    public void log(Level level, String tag, String msg, @Nullable Throwable th) {
        switch (level) {
            case VERBOSE: {
                if (th == null) {
                    Log.v(tag, msg);
                } else {
                    Log.v(tag, msg, th);
                }
                break;
            }
            case INFO: {
                if (th == null) {
                    Log.i(tag, msg);
                } else {
                    Log.i(tag, msg, th);
                }
                break;
            }
            case DEBUG: {
                if (th == null) {
                    Log.d(tag, msg);
                } else {
                    Log.d(tag, msg, th);
                }
                break;
            }
            case WARNING: {
                if (th == null) {
                    Log.w(tag, msg);
                } else {
                    Log.w(tag, msg, th);
                }
                break;
            }
            case ERROR: {
                if (th == null) {
                    Log.e(tag, msg);
                } else {
                    Log.e(tag, msg, th);
                }
                break;
            }
            case WTF: {
                if (th == null) {
                    Log.wtf(tag, msg);
                } else {
                    Log.wtf(tag, msg, th);
                }
                break;
            }
        }
    }

}
