package ua.at.tsvetkov.demokotlin

import android.app.Application
import ua.at.tsvetkov.util.logger.Log.setDisabled
import ua.at.tsvetkov.util.logger.Log.setStamp
import ua.at.tsvetkov.util.logger.LogComponents.enableComponentsChangesLogging

/**
 * Created by lordtao on 06.12.2017.
 */
class AppTaoLogDemo : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            setStamp(BuildConfig.GIT_SHA)
            enableComponentsChangesLogging(this)
        } else {
            setDisabled()
        }
    }

}