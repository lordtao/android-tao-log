package ua.at.tsvetkov.util.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ua.at.tsvetkov.util.Log.addInterceptor
import ua.at.tsvetkov.util.Log.e
import ua.at.tsvetkov.util.Log.getInterceptor
import ua.at.tsvetkov.util.Log.hasInterceptor
import ua.at.tsvetkov.util.R
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class LogFragment : Fragment() {

    private var isShowToolBar = false
    private var isToolBarOnTop = false
    private var logInterceptorId = 0
    private var logAdapter: LogAdapter? = null
    private var logToFileInterceptor: LogToFileInterceptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            logInterceptorId = arguments.getInt(LOG_FILE_INTERCEPTOR_ID)
            isShowToolBar = arguments.getBoolean(SHOW_TOOL_BAR)
            isToolBarOnTop = arguments.getBoolean(TOOL_BAR_ON_TOP)
            logToFileInterceptor = getInterceptor(logInterceptorId) as LogToFileInterceptor?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_log, container, false) as LinearLayout
        val recyclerView = context?.let { LogRecyclerView(it) }
        logAdapter = LogAdapter()
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = logAdapter
        //      DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
//      recyclerView.addItemDecoration(dividerItemDecoration);
        if (isShowToolBar) toolbarSetup(view, inflater, recyclerView)
        return view
    }

    private fun toolbarSetup(parent: LinearLayout, inflater: LayoutInflater, recyclerView: LogRecyclerView?) {
        val toolBar = inflater.inflate(R.layout.log_tool_bar, parent, false)
        if (isToolBarOnTop) {
            parent.addView(toolBar)
            parent.addView(recyclerView)
        } else {
            parent.addView(recyclerView)
            parent.addView(toolBar)
        }
        parent.findViewById<View>(R.id.logToolBarButtonClear).setOnClickListener {
            logToFileInterceptor!!.clear()
            loadLog()
        }
        parent.findViewById<View>(R.id.logToolBarButtonRecord).setOnClickListener {
            if (logToFileInterceptor!!.isEnabled) {
                (it as ImageButton).setImageResource(android.R.drawable.ic_media_pause)
                logToFileInterceptor!!.stopRecord()
            } else {
                (it as ImageButton).setImageResource(android.R.drawable.ic_media_play)
                logToFileInterceptor!!.startRecord()
            }
        }
        parent.findViewById<View>(R.id.logToolBarButtonRefresh).setOnClickListener { loadLog() }
        parent.findViewById<View>(R.id.logToolBarButtonSend).setOnClickListener { logToFileInterceptor!!.shareZippedLog(activity!!) }
    }

    private fun loadLog() {
        Thread {
            val file = File(logToFileInterceptor!!.logFileName)
            val logs = readLog(file)
            activity!!.runOnUiThread { logAdapter!!.setLogs(logs) }
        }.start()
    }

    private fun readLog(file: File): ArrayList<LogItem> {
        val logMessages = ArrayList<LogItem>()
        if (file.exists()) {
            var reader: BufferedReader? = null
            try {
                val fileReader = FileReader(file)
                reader = BufferedReader(fileReader)
                val sb = StringBuilder()
                for (i in 0..4) { // Skip header
                    reader.readLine()
                }
                var isEndOfMessage = false
                var line: String
                var inLogLineNumber = 0
                while (reader.ready()) {
                    sb.setLength(0)
                    isEndOfMessage = false
                    inLogLineNumber = 0
                    try {
                        while (!isEndOfMessage) {
                            inLogLineNumber++
                            line = reader.readLine()
                            sb.append(line.replace(" ▪ ", ""))
                            if (inLogLineNumber > 1) {
                                sb.append('\n')
                            }
                            if (line.contains(LogToFileInterceptor.LOG_END_OF_MESSAGE)) {
                                isEndOfMessage = true
                                val length = sb.length
                                sb.delete(length - 3, length)
                                logMessages.add(LogItem(sb.toString()))
                            }
                        }
                    } catch (ex: StringIndexOutOfBoundsException) {
                        e(ex)
                    }
                }
            } catch (e: IOException) {
                e("ReadLog", e)
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e(javaClass.name, e)
                    }
                }
            }
        }
        return logMessages
    }


    //   private String replaceLongLine(String line) {

    //      if (line.contains("=========") || line.contains("··········") || line.contains("---------")) {
    //         return line.substring(0, MAX_DECOR_LENGTH);
    //      }
    //      return line;
    //   }

    companion object {

        const val MAX_DECOR_LENGTH = 50
        private const val LOG_FILE_INTERCEPTOR_ID = "LOG_FILE_INTERCEPTOR_ID"
        private const val SHOW_TOOL_BAR = "SHOW_TOOL_BAR"
        private const val TOOL_BAR_ON_TOP = "TOOL_BAR_ON_TOP"

        @JvmOverloads
        @JvmStatic
        fun newInstance(context: Context, isShowToolBar: Boolean = true, isToolBarOnTop: Boolean = true): LogFragment {
            val logToFileInterceptor = LogToFileInterceptor(context)
            addInterceptor(logToFileInterceptor)
            return newInstance(logToFileInterceptor, isShowToolBar, isToolBarOnTop)
        }

        @JvmOverloads
        @JvmStatic
        fun newInstance(logToFileInterceptor: LogToFileInterceptor, isShowToolBar: Boolean = true, isToolBarOnTop: Boolean = true): LogFragment {
            return if (hasInterceptor(logToFileInterceptor)) {
                val fragment = LogFragment()
                val args = Bundle()
                args.putInt(LOG_FILE_INTERCEPTOR_ID, logToFileInterceptor.id)
                args.putBoolean(SHOW_TOOL_BAR, isShowToolBar)
                args.putBoolean(TOOL_BAR_ON_TOP, isToolBarOnTop)
                fragment.arguments = args
                fragment
            } else {
                throw IllegalArgumentException("The LogToFileInterceptor \"" + logToFileInterceptor.tag + "\" is not init in Log")
            }
        }

    }

}