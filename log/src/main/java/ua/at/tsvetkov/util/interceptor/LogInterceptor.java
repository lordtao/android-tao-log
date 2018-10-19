package ua.at.tsvetkov.util.interceptor;

public abstract class LogInterceptor {

    private String tag;

    private boolean isDisabled = false;

    public enum Level {
        VERBOSE, INFO, DEBUG, WARNING, ERROR, WTF;

        public char getShortName() {
            switch (this) {
                case VERBOSE: {
                    return 'V';
                }
                case INFO: {
                    return 'I';
                }
                case DEBUG: {
                    return 'D';
                }
                case WARNING: {
                    return 'W';
                }
                case ERROR: {
                    return 'E';
                }
                case WTF: {
                    return 'F';
                }
            }
            return '?';
        }
    }

    LogInterceptor() {
        tag = this.getClass().getName();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Intercepted log
     *
     * @param level     {@link Level} of logging.
     * @param tag       formatted tag.
     * @param msg       formatted message. If a Throwable is present, then included formatted throwable String.
     * @param throwable if it is present. For any yours usage.
     */
    public abstract void log(Level level, String tag, String msg, Throwable throwable);

    public void setDisabled() {
        this.isDisabled = true;
    }

    public void setEnabled() {
        this.isDisabled = false;
    }

    public boolean isEnabled() {
        return !isDisabled;
    }

    public int getId() {
        return hashCode();
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
