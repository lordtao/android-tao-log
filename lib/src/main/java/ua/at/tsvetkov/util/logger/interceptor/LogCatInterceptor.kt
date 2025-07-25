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

import android.util.Log

class LogCatInterceptor : LogInterceptor {
    override var enabled: Boolean = true

    override fun log(level: Level, tag: String?, msg: String?, throwable: Throwable?) {
        when (level) {
            Level.VERBOSE -> {
                Log.v(tag, msg ?: "null")
            }

            Level.INFO -> {
                Log.i(tag, msg ?: "null")
            }

            Level.DEBUG -> {
                Log.d(tag, msg ?: "null")
            }

            Level.WARNING -> {
                Log.w(tag, msg ?: "null")
            }

            Level.ERROR -> {
                Log.e(tag, msg ?: "null")
            }

            Level.WTF -> {
                Log.wtf(tag, msg ?: "null")
            }
        }
    }

}