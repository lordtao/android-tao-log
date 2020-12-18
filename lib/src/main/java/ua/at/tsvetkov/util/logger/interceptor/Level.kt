package ua.at.tsvetkov.util.logger.interceptor

enum class Level {
    VERBOSE, DEBUG, INFO, WARNING, ERROR, WTF;

    companion object {
        val names: ArrayList<String> = {
            val array = ArrayList<String>()
            values().forEach { array.add(it.name) }
            array
        }.invoke()
    }

}