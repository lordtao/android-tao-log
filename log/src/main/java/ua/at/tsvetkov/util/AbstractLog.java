package ua.at.tsvetkov.util;

import java.util.HashMap;
import java.util.HashSet;

import ua.at.tsvetkov.util.interceptor.LogCatInterceptor;
import ua.at.tsvetkov.util.interceptor.LogInterceptor;

class AbstractLog {

    protected static final HashMap<Integer, LogInterceptor> interceptors = new HashMap<>();

    protected static final LogCatInterceptor logCatInterceptor = new LogCatInterceptor();

    static {
        interceptors.put(logCatInterceptor.hashCode(), logCatInterceptor);
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
            interceptors.put(interceptor.hashCode(), interceptor);
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
            interceptors.put(logCatInterceptor.hashCode(), logCatInterceptor);
        }
    }

    public static HashMap<Integer, LogInterceptor> getInterceptors() {
        return interceptors;
    }

    public static LogInterceptor getInterceptor(int id) {
        synchronized (interceptors) {
            return interceptors.get(id);
        }
    }

    public static boolean hasInterceptor(int id) {
        synchronized (interceptors) {
            return interceptors.containsKey(id);
        }
    }

    public static boolean hasInterceptor(LogInterceptor interceptor) {
        synchronized (interceptors) {
            return interceptors.containsValue(interceptor);
        }
    }

    protected static void logToAll(LogInterceptor.Level level, String tag, String message) {
        synchronized (interceptors) {
            for (LogInterceptor interceptor : interceptors.values()) {
                if (interceptor.isEnabled()) {
                    interceptor.log(level, tag, message, null);
                }
            }
        }
    }

    protected static void logToAll(LogInterceptor.Level level, String tag, String message, Throwable throwable) {
        synchronized (interceptors) {
            for (LogInterceptor interceptor : interceptors.values()) {
                if (interceptor.isEnabled()) {
                    interceptor.log(level, tag, message, throwable);
                }
            }
        }
    }

}
