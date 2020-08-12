package ua.at.tsvetkov.util.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class LogRecyclerView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    private var logAdapter: LogAdapter? = null

    init {
        logAdapter = LogAdapter()
        val layoutManager = LinearLayoutManager(getContext())
        setLayoutManager(layoutManager)
        adapter = logAdapter
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        addItemDecoration(dividerItemDecoration)
    }

}