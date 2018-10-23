package ua.at.tsvetkov.util.ui;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import ua.at.tsvetkov.util.Level;
import ua.at.tsvetkov.util.interceptor.LogInterceptor;
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
        setDate(strs[0]);
        setLevel(strs[1].trim());
        setTag(strs[2]);
        setMessage(strs[3]);
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
