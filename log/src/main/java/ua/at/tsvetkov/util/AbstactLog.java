package ua.at.tsvetkov.util;

import java.util.HashSet;

import ua.at.tsvetkov.util.interceptor.LogCatInterceptor;
import ua.at.tsvetkov.util.interceptor.LogInterceptor;

class AbstactLog {

    static final HashSet<LogInterceptor> interceptors = new HashSet<>();

    private static final LogCatInterceptor logCatInterceptor = new LogCatInterceptor();

    static {
        interceptors.add(logCatInterceptor);
    }

    /**
     * Is LogCat logs disabled
     *
     * @return is disabled
     */
    public static boolean isDisabled() {
        return logCatInterceptor.isDisabled();
    }

    /**
     * Set LogCat logs disabled
     */
    public static void setDisabled() {
        logCatInterceptor.setDisabled();
    }

    /**
     * Is LogCat logs enabled
     *
     * @return is enabled
     */
    public static boolean isEnabled() {
        return logCatInterceptor.isEnabled();
    }

    /**
     * Set LogCat logs enabled
     */
    public static void setEnabled() {
        logCatInterceptor.setEnabled();
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

    public static void removeLogCatInterceptor() {
        synchronized (interceptors) {
            interceptors.remove(logCatInterceptor);
        }
    }

    public static void addLogCatInterceptor() {
        synchronized (interceptors) {
            interceptors.add(logCatInterceptor);
        }
    }

    public static HashSet<LogInterceptor> getInterceptors() {
        return interceptors;
    }

    protected static void logToAll(LogInterceptor.Level level, String tag, String message) {
        for (LogInterceptor interceptor : interceptors) {
            if (interceptor.isEnabled()) {
                interceptor.log(level, tag, message, null);
            }
        }
    }

    protected static void logToAll(LogInterceptor.Level level, String tag, String message, Throwable throwable) {
        for (LogInterceptor interceptor : interceptors) {
            if (interceptor.isEnabled()) {
                interceptor.log(level, tag, message, throwable);
            }
        }
    }

}
