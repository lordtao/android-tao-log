package ua.at.tsvetkov.util.ui;

import android.util.Log;

import ua.at.tsvetkov.util.Level;
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor;

/**
 * Created by Alexandr Tsvetkov on 10/22/18.
 */
public class LogItem {

   private String date;
   private Level level;
   private String tag;
   private String message;

   public LogItem(String logString) {
      String[] strs = logString.split(LogToFileInterceptor.LOG_SEPARATOR);
      if (strs.length == 4) {
         setDate(strs[0]);
         setLevel(strs[1].trim());
         setTag(strs[2]);
         setMessage(strs[3]);
      } else {
         Log.e("LogItem", "Wrong Log item string: " + logString);
      }
   }

   public String getDate() {
      return date;
   }

   public void setDate(String dateStr) {
      this.date = dateStr;
   }

   public Level getLevel() {
      return level;
   }

   public void setLevel(String levelStr) {
      try {
         this.level = Level.valueOf(levelStr);
      } catch (IllegalArgumentException e) {
         Log.e(getClass().getName(), e.getMessage() + " " + levelStr);
      }
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

}
