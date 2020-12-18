package ua.at.tsvetkov.util.logger.ui

import ua.at.tsvetkov.util.logger.interceptor.Level

/**
 * Created by Alexandr Tsvetkov on 08/02/20.
 */
class LogColorSets {
    /**
     * Default colors
     */
    private val colorSets = arrayOf(
            LogColor(-0x1, -0x848485, -0x848485, -0x848485),        // VERBOSE
            LogColor(-0x31191a, -0xffff1a, -0xffff1a, -0xffff1a),   // INFO
            LogColor(-0x2e0033, -0xff6df5, -0xff6df5, -0xff6df5),   // DEBUG
            LogColor(-0x129, -0x286b00, -0x286b00, -0x286b00),      // WARNING
            LogColor(-0x3932, -0x810000, -0x810000, -0x810000),     // ERROR
            LogColor(-0x1, -0x810000, -0x810000, -0x810000)         // WTF
    )

    private fun setVerbose(logColor: LogColor) {
        colorSets[Level.VERBOSE.ordinal] = logColor
    }

    fun setInfo(logColor: LogColor) {
        colorSets[Level.INFO.ordinal] = logColor
    }

    fun setDebug(logColor: LogColor) {
        colorSets[Level.DEBUG.ordinal] = logColor
    }

    fun setWarning(logColor: LogColor) {
        colorSets[Level.WARNING.ordinal] = logColor
    }

    fun setError(logColor: LogColor) {
        colorSets[Level.ERROR.ordinal] = logColor
    }

    fun setWtf(logColor: LogColor) {
        colorSets[Level.WTF.ordinal] = logColor
    }

    fun getColorSet(level: Level) = colorSets[level.ordinal]

}