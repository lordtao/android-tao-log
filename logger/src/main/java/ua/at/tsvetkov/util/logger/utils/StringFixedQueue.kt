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
package ua.at.tsvetkov.util.logger.utils

/**
 * A fixed-size circular buffer for Strings that automatically evicts the oldest elements
 * when the capacity is reached. New elements are added to the end,
 * and old ones are removed from the beginning.
 */
class StringFixedQueue(val capacity: Int) {
    private val buffer = arrayOfNulls<String>(capacity)
    private var head = 0
    var size = 0
        private set

    /**
     * Adds a new string to the end of the queue.
     * If the buffer is full, the oldest string is overwritten.
     */
    fun add(value: String) {
        if (size < capacity) {
            buffer[(head + size) % capacity] = value
            size++
        } else {
            buffer[head] = value
            head = (head + 1) % capacity
        }
    }

    /**
     * Clears all strings from the buffer and resets the state.
     */
    fun clear() {
        buffer.fill(null)
        head = 0
        size = 0
    }

    /**
     * Returns the current strings as a List in chronological order.
     */
    fun toList(): List<String> {
        return List(size) { i ->
            buffer[(head + i) % capacity]!!
        }
    }

    /**
     * Returns the current strings as an Array in chronological order.
     */
    fun toArray(): Array<String> {
        return Array(size) { i ->
            buffer[(head + i) % capacity]!!
        }
    }
}