package ua.at.tsvetkov.util.interceptor;

public abstract class LogInterceptor {

    private boolean isDisabled = false;

    public enum Level {VERBOSE, INFO, DEBUG, WARNING, ERROR, WTF}

    /**
     * Intercepted log
     *
     * @param level     {@link Level} of logging.
     * @param tag       formatted tag.
     * @param msg       formatted message. If a Throwable is present, then included formatted throwable String.
     * @param throwable if it is present. For any yours usage.
     */
    public abstract void log(Level level, String tag, String msg, Throwable throwable);

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean isEnabled() {
        return !isDisabled;
    }


    public boolean isDisabled() {
        return isDisabled;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " {" +
                "isDisabled=" + isDisabled +
                '}';
    }

}
