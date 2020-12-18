package ua.at.tsvetkov.util.logger.interceptor

interface LogInterceptor {

    /**
     * State of the logger
     */
    var enabled: Boolean

    /**
     * Intercepted log
     *
     * @param level     [Level] of logging.
     * @param tag       formatted tag.
     * @param msg       formatted message. If a Throwable is present, then included formatted throwable String.
     * @param throwable if it is present. For any yours usage.
     */
    fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?)

    fun getTag():String{
        return this.javaClass.simpleName + '@' + this.hashCode()
    }

}

/**
 * For internal usage. Trunk log decor for a log string
 */
interface TrunkLongLogDecorInterface {

    fun trunkLongDecor(line: String, maxDecorLength: Int): String {
        if (line.length < maxDecorLength) return line
        if (line.contains("=========") || line.contains("··········") || line.contains("---------")) {
            return line.substring(line.length - maxDecorLength, line.length)
        }
        return line
    }

}

