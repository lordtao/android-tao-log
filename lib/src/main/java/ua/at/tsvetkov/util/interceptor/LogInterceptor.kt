package ua.at.tsvetkov.util.interceptor

abstract class LogInterceptor {

    var tag: String = this.javaClass.name
    var isDisabled = false
        private set

    /**
     * Intercepted log
     *
     * @param level     [Level] of logging.
     * @param tag       formatted tag.
     * @param msg       formatted message. If a Throwable is present, then included formatted throwable String.
     * @param throwable if it is present. For any yours usage.
     */
    abstract fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?)

    fun setDisabled() {
        isDisabled = true
    }

    fun setEnabled() {
        isDisabled = false
    }

    val isEnabled: Boolean
        get() = !isDisabled
    val id: Int
        get() = hashCode()

    override fun toString(): String {
        return tag + " {" +
                "isDisabled=" + isDisabled +
                '}'
    }

}