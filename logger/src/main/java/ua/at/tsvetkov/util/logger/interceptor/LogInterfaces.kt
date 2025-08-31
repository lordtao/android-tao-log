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

    @Suppress("unused")
    fun getTag(): String {
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

