package ua.at.tsvetkov.util.logger.utils

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ua.at.tsvetkov.util.logger.utils.Format.getFormattedMessage
import ua.at.tsvetkov.util.logger.utils.Format.getFragmentsStackInfo

internal class FragmentLifecycleLogger : FragmentManager.FragmentLifecycleCallbacks() {

    private var parent = ""

    override fun onFragmentAttached(fm: FragmentManager, fr: Fragment, context: Context) {
        super.onFragmentAttached(fm, fr, context)
        parent = context.javaClass.simpleName
        val backStackCount = fm.backStackEntryCount
        val info = getFragmentsStackInfo(fm, fr.javaClass.simpleName, backStackCount)
        val data = Format.getLocationContainer()
        data.stackTraceNumber = 3
        Log.v(parent, getFormattedMessage(data, info, "Fragments stack [$backStackCount]").toString())
    }

    override fun onFragmentDetached(fm: FragmentManager, fr: Fragment) {
        super.onFragmentDetached(fm, fr)
        val backStackCount = fm.backStackEntryCount
        val info = getFragmentsStackInfo(fm, fr.javaClass.simpleName, backStackCount)
        val data = Format.getLocationContainer()
        data.stackTraceNumber = 3
        Log.v(parent, getFormattedMessage(data, info, "Fragments stack [$backStackCount]").toString())
    }

}