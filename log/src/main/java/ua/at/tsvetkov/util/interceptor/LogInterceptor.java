package ua.at.tsvetkov.util.interceptor;

import ua.at.tsvetkov.util.Level;

public abstract class LogInterceptor {

    private String tag;

    private boolean isDisabled = false;

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
