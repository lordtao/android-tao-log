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
package ua.at.tsvetkov.util.logger.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.at.tsvetkov.util.logger.Log
import ua.at.tsvetkov.util.logger.R
import ua.at.tsvetkov.util.logger.interceptor.LogToMemoryCacheInterceptor
import ua.at.tsvetkov.util.logger.ui.LogAdapter.LogViewHolder
import java.util.LinkedList

class LogAdapter(val colorsSet: LogColorSets = LogColorSets()) : RecyclerView
.Adapter<LogViewHolder>(),
    LogToMemoryCacheInterceptor.NewLogEventListener {

    private var items: LinkedList<LogItem> = LinkedList<LogItem>()
    private var interceptor: LogToMemoryCacheInterceptor =
        LogToMemoryCacheInterceptor.sharedInstance

    init {
        interceptor.listener = this
        applyFilteredList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_log_card, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logItem = items[position]
        val colorSet = colorsSet.getColorSet(logItem.level)

        holder.date?.text = logItem.dateString
        holder.tag?.text = logItem.tag
        holder.message?.text = logItem.message

        holder.container?.setBackgroundColor(colorSet.background)
        holder.date?.setTextColor(colorSet.dateColor)
        holder.tag?.setTextColor(colorSet.tagColor)
        holder.message?.setTextColor(colorSet.messageColor)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun newEvent(logItem: LogItem) {
        if (isPassFilter(logItem)) {
            val position = items.size
            items.add(logItem)
            notifyItemInserted(position)
        }
    }

    @Suppress("unused")
    override fun removedFirstElements(count: Int) {
        for (i in 1..count) {
            items.removeFirst()
        }
        notifyItemRangeRemoved(0, count)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun applyFilteredList() {
        synchronized(interceptor.list) {
            val list = interceptor.list.filter { logItem -> isPassFilter(logItem) }
            items.clear()
            items.addAll(list)
        }
        notifyDataSetChanged()
    }

    private fun isPassFilter(logItem: LogItem): Boolean {
        var isGood = logItem.level >= interceptor.filterLevel
        if (isGood) {
            if (!interceptor.filterSearchString.isNullOrBlank()) {
                interceptor.filterSearchString?.let {
                    isGood = logItem.tag.contains(it, true) || logItem.message.contains(it, true)
                }
            }
        }
        return isGood
    }

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView? = view.findViewById(R.id.date)
        val tag: TextView? = view.findViewById(R.id.tag)
        val message: TextView? = view.findViewById(R.id.message)
        val container: View? = view.findViewById(R.id.container)

        init {
            if (date == null) Log.w("Your log item view is not contains TextView with \"data\" id")
            if (tag == null) Log.w("Your log item view is not contains TextView with \"tag\" id")
            if (message == null) Log.w("Your log item view is not contains TextView with \"message\" id")
            if (container == null) Log.w("Your log item view is not contains root View with \"container\" id")
        }
    }

}