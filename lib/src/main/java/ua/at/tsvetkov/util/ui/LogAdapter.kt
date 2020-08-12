package ua.at.tsvetkov.util.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.at.tsvetkov.util.Log
import ua.at.tsvetkov.util.R
import ua.at.tsvetkov.util.ui.LogAdapter.LogViewHolder
import java.util.*

class LogAdapter(val isCard: Boolean = true,
                 val colorsSet: LogColorSets = LogColorSets(),
                 val logItems: ArrayList<LogItem> = ArrayList(),
                 var itemView: View? = null) : RecyclerView.Adapter<LogViewHolder>() {

    fun setLogs(logs: ArrayList<LogItem>?) {
        logItems.clear()
        logs?.let {
            logItems.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun addToLog(logs: ArrayList<LogItem>?) {
        logs?.let {
            logItems.addAll(it)
            notifyDataSetChanged()
        }
    }

    fun addToLog(log: LogItem?) {
        log?.let {
            logItems.add(it)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        itemView?.let { return LogViewHolder(it) }

        val view = if (isCard) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_log_card, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        }
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logItem = logItems[position]
        val colorSet = colorsSet.getColorSet(logItem.level)

        holder.date?.text = logItem.date
        holder.tag?.text = logItem.tag
        holder.message?.text = logItem.message

        holder.container?.setBackgroundColor(colorSet.background)
        holder.date?.setTextColor(colorSet.dateColor)
        holder.tag?.setTextColor(colorSet.tagColor)
        holder.message?.setTextColor(colorSet.messageColor)
    }

    override fun getItemCount(): Int {
        return logItems.size
    }

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView? = view.findViewById(R.id.date)
        val tag: TextView? = view.findViewById(R.id.tag)
        val message: TextView? = view.findViewById(R.id.message)
        val container: View? = view.findViewById(R.id.container)

        init {
            if (date == null) Log.w("Your log item is not contains TextView with \"data\" id")
            if (tag == null) Log.w("Your log item is not contains TextView with \"tag\" id")
            if (message == null) Log.w("Your log item is not contains TextView with \"message\" id")
            if (container == null) Log.w("Your log item is not contains root View with \"container\" id")
        }
    }

}