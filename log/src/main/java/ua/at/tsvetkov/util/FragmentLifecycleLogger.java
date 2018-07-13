package ua.at.tsvetkov.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

class FragmentLifecycleLogger extends FragmentManager.FragmentLifecycleCallbacks {

   String parent = "";
   String info = null;

   @Override
   public void onFragmentAttached(FragmentManager fm, Fragment fr, Context context) {
      super.onFragmentAttached(fm, fr, context);
      parent = context.getClass().getSimpleName();
      int backStackCount = fm.getBackStackEntryCount();
      info = Format.getFragmentsStackInfo(fm, "attached " + fr.getClass().getSimpleName(), backStackCount);
      android.util.Log.i(parent, Format.getFormattedMessage(info, "Fragments stack [" + backStackCount + "]").toString());
   }

   @Override
   public void onFragmentDetached(FragmentManager fm, Fragment fr) {
      super.onFragmentDetached(fm, fr);
      int backStackCount = fm.getBackStackEntryCount();
      info = Format.getFragmentsStackInfo(fm, "detached " + fr.getClass().getSimpleName(), backStackCount);
      android.util.Log.i(parent, Format.getFormattedMessage(info, "Fragments stack [" + backStackCount + "]").toString());
   }

}
