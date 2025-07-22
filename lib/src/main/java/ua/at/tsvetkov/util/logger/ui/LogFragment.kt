package ua.at.tsvetkov.util.logger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ua.at.tsvetkov.util.R
import ua.at.tsvetkov.util.databinding.FragmentLogBinding
import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.interceptor.LogToFileInterceptor
import ua.at.tsvetkov.util.logger.interceptor.LogToMemoryCacheInterceptor
import ua.at.tsvetkov.util.logger.utils.LogZipper

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var logAdapter: LogAdapter
    private lateinit var logZipper: LogZipper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logAdapter = LogAdapter()

        // LogAdapter using shared instance of LogToMemoryCacheInterceptor
        layoutManager = LinearLayoutManager(context)
        with(binding) {
            fragmentLogMessagesRecycleView.layoutManager = layoutManager
            fragmentLogMessagesRecycleView.adapter = logAdapter

            logToolBarButtonClear.setOnClickListener { logClear() }
            logToolBarButtonRecord.setOnClickListener { logRecordOrPause() }
            logToolBarButtonSearch.setOnClickListener { logSearch() }
            logToolBarButtonShare.setOnClickListener { logShare() }
            logToolBarButtonScroll.setOnClickListener { scrollDown() }
            logToolBarButtonSearchClear.setOnClickListener { logSearchClear() }

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner,
                Level.names
            )
            adapter.setDropDownViewResource(R.layout.item_spinner)
            logToolBarLevelSpinner.adapter = adapter
            logToolBarLevelSpinner.setSelection(0)
            logToolBarLevelSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        var1: AdapterView<*>?,
                        var2: View?,
                        var3: Int,
                        var4: Long
                    ) {
                        LogToMemoryCacheInterceptor.sharedInstance.filterLevel =
                            Level.valueOf(logToolBarLevelSpinner.selectedItem.toString())
                        logAdapter.applyFilteredList()
                    }

                    override fun onNothingSelected(var1: AdapterView<*>?) {}
                }

            logZipper = LogZipper(LogToFileInterceptor.getSharedInstance(requireContext()))
        }
    }

    override fun onResume() {
        super.onResume()
        logAdapter.applyFilteredList()
    }

    fun scrollDown() {
        binding.fragmentLogMessagesRecycleView.scrollToPosition(logAdapter.itemCount - 1)
    }

    private fun logSearchClear() {
        binding.logToolBarTextSearch.setText("")
        logSearch()
    }

    private fun logShare() {
        logZipper.shareZip(requireActivity())
    }

    private fun logSearch() {
        LogToMemoryCacheInterceptor.sharedInstance.filterSearchString =
            binding.logToolBarTextSearch.text
        logAdapter.applyFilteredList()
    }

    private fun logRecordOrPause() {
        val isEnabled = !LogToMemoryCacheInterceptor.sharedInstance.enabled
        LogToMemoryCacheInterceptor.sharedInstance.enabled = isEnabled
        if (isEnabled) {
            binding.logToolBarButtonRecord.setImageResource(R.drawable.ic_pause)
            Toast.makeText(context, "Log show started", Toast.LENGTH_LONG).show()
        } else {
            binding.logToolBarButtonRecord.setImageResource(R.drawable.ic_play)
            Toast.makeText(context, "Log show paused", Toast.LENGTH_LONG).show()
        }
    }

    private fun logClear() {
        LogToMemoryCacheInterceptor.sharedInstance.clear()
    }

}

