package ua.at.tsvetkov.demo

import ua.at.tsvetkov.annotations.ToLog
import ua.at.tsvetkov.util.Log

/**
 * Created by Alexandr Tsvetkov on 3/28/2018.
 */
class Test constructor(name: String) {

    private val mName = name

    @ToLog
    fun getName(): String {
        Log.v("Name=$mName")
        return mName
    }


}