package ua.at.tsvetkov.util

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ua.at.tsvetkov.util.Format.getFormattedMessage
import ua.at.tsvetkov.util.Format.getFragmentsStackInfo

internal class FragmentLifecycleLogger : FragmentManager.FragmentLifecycleCallbacks() {

    private var parent = ""
    private var info: String? = null

    override fun onFragmentAttached(fm: FragmentManager, fr: Fragment, context: Context) {
        super.onFragmentAttached(fm, fr, context)
        parent = context.javaClass.simpleName
        val backStackCount = fm.backStackEntryCount
        info = getFragmentsStackInfo(fm, "attached " + fr.javaClass.simpleName, backStackCount)
        Log.i(parent, getFormattedMessage(info!!, "Fragments stack [$backStackCount]").toString())
    }

    override fun onFragmentDetached(fm: FragmentManager, fr: Fragment) {
        super.onFragmentDetached(fm, fr)
        val backStackCount = fm.backStackEntryCount
        info = getFragmentsStackInfo(fm, "detached " + fr.javaClass.simpleName, backStackCount)
        Log.i(parent, getFormattedMessage(info!!, "Fragments stack [$backStackCount]").toString())
    }

}