package ua.at.tsvetkov.util;

import java.util.HashSet;

import ua.at.tsvetkov.util.interceptor.LogCatInterceptor;
import ua.at.tsvetkov.util.interceptor.LogInterceptor;

class AbstactLog {

    static final HashSet<LogInterceptor> interceptors = new HashSet<>();

    static volatile boolean isDisabled = false;

    private static final LogCatInterceptor logCatInterceptor= new LogCatInterceptor();

    static  {
        interceptors.add(logCatInterceptor);
    }

    /**
     * Is all logs disabled
     *
     * @return is disabled
     */
    public static boolean isDisabled() {
        return isDisabled;
    }

    /**
     * Set all logs disabled or enabled
     *
     * @param isDisable is disabled
     */
    public static void setDisabled(boolean isDisable) {
        isDisabled = isDisable;
    }

    public static void addInterceptor(LogInterceptor interceptor) {
        synchronized (interceptors) {
            interceptors.add(interceptor);
        }
    }

    public static void removeInterceptor(LogInterceptor interceptor) {
        synchronized (interceptors) {
            interceptors.remove(interceptor);
        }
    }

    public static void removeInterceptors() {
        synchronized (interceptors) {
            interceptors.clear();
        }
    }

    public static void removeLogCatInterceptor(){
        synchronized (interceptors) {
            interceptors.remove(logCatInterceptor);
        }
    }

    public static void addLogCatInterceptor(){
        synchronized (interceptors) {
            interceptors.add(logCatInterceptor);
        }
    }

    public static HashSet<LogInterceptor> getInterceptors(){
        return interceptors;
    }

    static void logToAll(LogInterceptor.Level level, String tag, String message) {
        for (LogInterceptor interceptor : interceptors) {
            if (interceptor.isEnabled()) {
                interceptor.log(level, tag, message, null);
            }
        }
    }

    static void logToAll(LogInterceptor.Level level, String tag, String message, Throwable throwable) {
        for (LogInterceptor interceptor : interceptors) {
            if (interceptor.isEnabled()) {
                interceptor.log(level, tag, message, throwable);
            }
        }
    }

}
