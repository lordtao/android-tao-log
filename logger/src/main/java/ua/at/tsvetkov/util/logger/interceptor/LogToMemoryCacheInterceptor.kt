/******************************************************************************
 * Copyright (c) 2010-2025 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * https://opensource.org/license/mit
 *
 * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 *
 * Project:
 * TAO Log
 *
 * License agreement:
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ua.at.tsvetkov.util.logger.interceptor

import kotlinx.coroutines.DelicateCoroutinesApi
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
class LogToMemoryCacheInterceptor(
    var listener: NewLogEventListener? = null,
    val maxLogSize: Int = MAX_LOG_CACHE_SIZE,
    val portion: Int = PORTION_FOR_CLEARING
) : LogInterceptor, TrunkLongLogDecorInterface {

    override var enabled: Boolean = true
    var list = LinkedList<LogItem>()
    var filterLevel = Level.VERBOSE
    var filterSearchString: CharSequence? = null

    @OptIn(DelicateCoroutinesApi::class)
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
            repeat(portion) {
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