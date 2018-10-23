package ua.at.tsvetkov.util.ui;

import ua.at.tsvetkov.util.Level;

/**
 * Created by Alexandr Tsvetkov on 10/22/18.
 */
public class LogColorsSet {

    private LogColor[] logColors = new LogColor[6];

    public static LogColorsSet createLightSet() {
        LogColorsSet logColorsSet = new LogColorsSet();
        logColorsSet.setVerbose(0xFFFFFFFF, 0xFF7B7B7B);
        logColorsSet.setInfo(0xFFD1FFCD, 0xFF00920B);
        logColorsSet.setDebug(0xFFCEE6E6, 0xFF0000E6);
        logColorsSet.setWarning(0xFFFFFED7, 0xFFD79500);
        logColorsSet.setError(0xFFFFC6CE, 0xFF7F0000);
        logColorsSet.setWtf(0xFFFFFFFF, 0xFF7F0000);
        return logColorsSet;
    }

    public LogColor getColor(Level level){
        return logColors[level.ordinal()];
    }

    public void setVerbose(int background, int foreground) {
        logColors[Level.VERBOSE.ordinal()] = new LogColor(background, foreground);
    }

    public void setInfo(int background, int foreground) {
        logColors[Level.INFO.ordinal()] = new LogColor(background, foreground);
    }

    public void setDebug(int background, int foreground) {
        logColors[Level.DEBUG.ordinal()] = new LogColor(background, foreground);
    }

    public void setWarning(int background, int foreground) {
        logColors[Level.WARNING.ordinal()] = new LogColor(background, foreground);
    }

    public void setError(int background, int foreground) {
        logColors[Level.ERROR.ordinal()] = new LogColor(background, foreground);
    }

    public void setWtf(int background, int foreground) {
        logColors[Level.WTF.ordinal()] = new LogColor(background, foreground);
    }

    public LogColor getVerbose() {
        return logColors[Level.VERBOSE.ordinal()];
    }

    public LogColor getInfo() {
        return logColors[Level.INFO.ordinal()];
    }

    public LogColor getDebug() {
        return logColors[Level.DEBUG.ordinal()];
    }

    public LogColor getWarning() {
        return logColors[Level.WARNING.ordinal()];
    }

    public LogColor getError() {
        return logColors[Level.ERROR.ordinal()];
    }

    public LogColor getWtf() {
        return logColors[Level.WTF.ordinal()];
    }
}
