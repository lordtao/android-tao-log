package ua.at.tsvetkov.util.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ua.at.tsvetkov.util.Log;
import ua.at.tsvetkov.util.R;
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor;

public class LogFragment extends Fragment {

   public static final int MAX_DECOR_LENGTH = 50;
   private static final String LOG_FILE_INTERCEPTOR_ID = "LOG_FILE_INTERCEPTOR_ID";
   private static final String SHOW_TOOL_BAR = "SHOW_TOOL_BAR";
   private static final String TOOL_BAR_ON_TOP = "TOOL_BAR_ON_TOP";
   private boolean isShowToolBar;
   private boolean isToolBarOnTop;
   private int logInterceptorId;

   private LogAdapter logAdapter;
   private LogToFileInterceptor logToFileInterceptor;

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
      if (logToFileInterceptor == null) {
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
      initArgs();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_log, container, false);
      RecyclerView recyclerView = view.findViewById(R.id.fragmentLogMessagesRecycleView);

      logAdapter = new LogAdapter(LogColorsSet.createLightSet());
      LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setAdapter(logAdapter);
      DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
      recyclerView.addItemDecoration(dividerItemDecoration);

      toolbarSetup(view, inflater);

      return view;
   }

   private void toolbarSetup(View parent, LayoutInflater inflater) {
      if (isShowToolBar) {
         if (isToolBarOnTop) {
            parent.findViewById(R.id.fragmentLogBottomToolBar).setVisibility(View.GONE);
         } else {
            parent.findViewById(R.id.fragmentLogTopToolBar).setVisibility(View.GONE);
         }
         parent.findViewById(R.id.logToolBarButtonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               logToFileInterceptor.clear();
               loadLog();
            }
         });
         parent.findViewById(R.id.logToolBarButtonRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (logToFileInterceptor.isEnabled()) {
                  ((ImageButton)view).setImageResource(android.R.drawable.ic_media_pause);
                  logToFileInterceptor.stopRecord();
               } else {
                  ((ImageButton)view).setImageResource(android.R.drawable.ic_media_play);
                  logToFileInterceptor.startRecord();
               }
            }
         });
         parent.findViewById(R.id.logToolBarButtonRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               loadLog();
            }
         });
         parent.findViewById(R.id.logToolBarButtonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               logToFileInterceptor.shareZippedLog(getActivity());
            }
         });
      }
   }

   private void initArgs() {
      Bundle arguments = getArguments();
      if (arguments != null) {
         logInterceptorId = arguments.getInt(LOG_FILE_INTERCEPTOR_ID);
         isShowToolBar = arguments.getBoolean(SHOW_TOOL_BAR);
         isToolBarOnTop = arguments.getBoolean(TOOL_BAR_ON_TOP);
         logToFileInterceptor = (LogToFileInterceptor) Log.getInterceptor(logInterceptorId);
      }
   }

   public void loadLog() {
      new Thread(new Runnable() {

         @Override
         public void run() {
            File file = new File(logToFileInterceptor.getLogFileName());
            final ArrayList<LogItem> logs = readLog(file);
            getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  logAdapter.setData(logs);
               }
            });
         }

      }).start();
   }

   private ArrayList<LogItem> readLog(File file) {
      ArrayList<LogItem> logMessages = new ArrayList<>();
      if(file.exists()) {
         BufferedReader reader = null;
         try {
            FileReader filefileReader = new FileReader(file);
            reader = new BufferedReader(filefileReader);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) { // Skip header
               reader.readLine();
            }
            boolean isEndOfMessage = false;
            String line;
            int inLogLineNumber = 0;
            while (reader.ready()) {
               sb.setLength(0);
               isEndOfMessage = false;
               inLogLineNumber = 0;
               try {
                  while (!isEndOfMessage) {
                     inLogLineNumber++;
                     line = reader.readLine();
                     sb.append(line.replace(" ▪ ", ""));
                     if (inLogLineNumber > 1) {
                        sb.append('\n');
                     }
                     if (line.contains(LogToFileInterceptor.LOG_END_OF_MESSAGE)) {
                        isEndOfMessage = true;
                        int length = sb.length();
                        sb.delete(length - 3, length);
                        logMessages.add(new LogItem(sb.toString()));
                     }
                  }
               } catch (StringIndexOutOfBoundsException ex) {
                  Log.e(ex);
               }
            }
         } catch (IOException e) {
            Log.e("ReadLog", e);
         } finally {
            if (reader != null) {
               try {
                  reader.close();
               } catch (IOException e) {
                  Log.e(getClass().getName(), e);
               }
            }
         }
      }
      return logMessages;
   }

//   private String replaceLongLine(String line) {
//      if (line.contains("=========") || line.contains("··········") || line.contains("---------")) {
//         return line.substring(0, MAX_DECOR_LENGTH);
//      }
//      return line;
//   }

}
