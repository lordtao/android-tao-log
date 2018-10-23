package ua.at.tsvetkov.util;

/**
 * Created by Alexandr Tsvetkov on 10/22/18.
 */
public enum Level {
    VERBOSE, INFO, DEBUG, WARNING, ERROR, WTF;

    public char getShortName() {
        switch (this) {
            case VERBOSE: {
                return 'V';
            }
            case INFO: {
                return 'I';
            }
            case DEBUG: {
                return 'D';
            }
            case WARNING: {
                return 'W';
            }
            case ERROR: {
                return 'E';
            }
            case WTF: {
                return 'F';
            }
        }
        return '?';
    }
}