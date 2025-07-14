package ua.at.tsvetkov.demo

import android.app.Application
import ua.at.tsvetkov.util.BuildConfig
import ua.at.tsvetkov.util.logger.Log
import ua.at.tsvetkov.util.logger.LogComponents

/**
 * Created by lordtao on 09.17.2020.
 */
class AppLogDemoKotlin : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            LogComponents.enableComponentsChangesLogging(this)
        } else {
            Log.setDisabled()
        }
    }
}