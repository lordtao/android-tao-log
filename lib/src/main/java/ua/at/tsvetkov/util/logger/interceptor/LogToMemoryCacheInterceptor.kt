package ua.at.tsvetkov.util.logger.interceptor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import ua.at.tsvetkov.util.logger.Log
import ua.at.tsvetkov.util.logger.ui.LogItem
import java.util.*

/**
 * Create the Log Interceptor which storing in memory latest logs.
 *
 * @param listener
 * @param maxLogSize maximum logs amount stored in memory cache
 * @param portion
 */
class LogToMemoryCacheInterceptor(var listener: NewLogEventListener? = null,
                                  val maxLogSize: Int = MAX_LOG_CACHE_SIZE,
                                  val portion: Int = PORTION_FOR_CLEARING)
    : LogInterceptor, TrunkLongLogDecorInterface {

    override var enabled: Boolean = true
    var list = LinkedList<LogItem>()
    var filterLevel = Level.VERBOSE
    var filterSearchString: CharSequence? = null

    @InternalCoroutinesApi
    override fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?) {
        val logItem = LogItem(Date(), level, tag, msg)
        list.add(logItem)
        GlobalScope.launch(Dispatchers.Main) {
            update(logItem)
        }
    }

    @Synchronized
    private fun update(logItem: LogItem) {
        if (list.size > maxLogSize) {
            for (i in 1..portion) {
                list.removeFirst()
            }
            listener?.removedFirstElements(portion)
        }
        listener?.newEvent(logItem)
    }

    fun clear() {
        list.clear()
        listener?.clear()
    }

    /**
     * Callback for a new LogItem. Please remember, not all calls can be from MainThread
     */
    interface NewLogEventListener {
        fun newEvent(logItem: LogItem)
        fun removedFirstElements(count: Int)
        fun clear()
    }

    companion object {

        const val MAX_LOG_CACHE_SIZE = 1000
        const val PORTION_FOR_CLEARING = 10

        val sharedInstance: LogToMemoryCacheInterceptor by lazy {
            val interceptor = LogToMemoryCacheInterceptor()
            Log.addInterceptor(interceptor)
            interceptor
        }

    }

}