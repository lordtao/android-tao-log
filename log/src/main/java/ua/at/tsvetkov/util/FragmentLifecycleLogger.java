package ua.at.tsvetkov.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

class FragmentLifecycleLogger extends FragmentManager.FragmentLifecycleCallbacks {

   private String parent = "";
   private String info = null;

   @Override
   public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment fr, @NonNull Context context) {
      super.onFragmentAttached(fm, fr, context);
      parent = context.getClass().getSimpleName();
      int backStackCount = fm.getBackStackEntryCount();
      info = Format.getFragmentsStackInfo(fm, "attached " + fr.getClass().getSimpleName(), backStackCount);
      android.util.Log.i(parent, Format.getFormattedMessage(info, "Fragments stack [" + backStackCount + "]").toString());
   }

   @Override
   public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment fr) {
      super.onFragmentDetached(fm, fr);
      int backStackCount = fm.getBackStackEntryCount();
      info = Format.getFragmentsStackInfo(fm, "detached " + fr.getClass().getSimpleName(), backStackCount);
      android.util.Log.i(parent, Format.getFormattedMessage(info, "Fragments stack [" + backStackCount + "]").toString());
   }

}
