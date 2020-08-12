package ua.at.tsvetkov.util.interceptor

enum class Level {
    VERBOSE, INFO, DEBUG, WARNING, ERROR, WTF;

    val shortName: Char
        get() {
            return when (this) {
                VERBOSE -> {
                    'V'
                }
                INFO -> {
                    'I'
                }
                DEBUG -> {
                    'D'
                }
                WARNING -> {
                    'W'
                }
                ERROR -> {
                    'E'
                }
                WTF -> {
                    'F'
                }
            }
        }
}