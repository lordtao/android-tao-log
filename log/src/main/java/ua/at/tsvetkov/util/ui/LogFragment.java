package ua.at.tsvetkov.util.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ua.at.tsvetkov.util.Log;
import ua.at.tsvetkov.util.R;
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor;

public class LogFragment extends Fragment {

    private static final String LOG_FILE_INTERCEPTOR_ID = "LOG_FILE_INTERCEPTOR_ID";
    private static final String SHOW_TOOL_BAR = "SHOW_TOOL_BAR";
    private static final String TOOL_BAR_ON_TOP = "TOOL_BAR_ON_TOP";

    private LogToFileInterceptor interceptor;
    private boolean isShowToolBar;
    private boolean isToolBarOnTop;

    private OnFragmentInteractionListener mListener;

    public LogFragment() {

    }

    public static LogFragment newInstance(Context context) {
        return newInstance(context, true, true);
    }

    public static LogFragment newInstance(Context context, boolean isShowToolBar, boolean isToolBarOnTop) {
        LogToFileInterceptor logToFileInterceptor = new LogToFileInterceptor(context);
        Log.addInterceptor(logToFileInterceptor);
        return newInstance(logToFileInterceptor, isShowToolBar, isToolBarOnTop);
    }

    public static LogFragment newInstance(LogToFileInterceptor logToFileInterceptor) {
        return newInstance(logToFileInterceptor, true, true);
    }

    public static LogFragment newInstance(LogToFileInterceptor logToFileInterceptor, boolean isShowToolBar, boolean isToolBarOnTop) {
        if (logToFileInterceptor != null) {
            throw new NullPointerException("The LogToFileInterceptor is null");
        }
        if (Log.hasInterceptor(logToFileInterceptor)) {
            LogFragment fragment = new LogFragment();
            Bundle args = new Bundle();
            args.putInt(LOG_FILE_INTERCEPTOR_ID, logToFileInterceptor.getId());
            args.putBoolean(SHOW_TOOL_BAR, isShowToolBar);
            args.putBoolean(TOOL_BAR_ON_TOP, isToolBarOnTop);
            fragment.setArguments(args);
            return fragment;
        } else {
            throw new IllegalArgumentException("The LogToFileInterceptor \"" + logToFileInterceptor.getTag() + "\" is not init in Log");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int id = arguments.getInt(LOG_FILE_INTERCEPTOR_ID);
            isShowToolBar = arguments.getBoolean(SHOW_TOOL_BAR);
            isToolBarOnTop = arguments.getBoolean(TOOL_BAR_ON_TOP);
            interceptor = (LogToFileInterceptor) Log.getInterceptor(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(inflater.getContext());
        if (isShowToolBar) {
            View toolbar = inflater.inflate(R.layout.log_tool_bar, view);
            if (isToolBarOnTop) {
                view.addView(toolbar);
                addLogView(view);
            } else {
                addLogView(view);
                view.addView(toolbar);
            }
        }
        return view;
    }

    private void addLogView(LinearLayout view) {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
