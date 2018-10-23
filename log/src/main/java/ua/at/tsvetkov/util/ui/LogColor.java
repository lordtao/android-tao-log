package ua.at.tsvetkov.util.ui;

/**
 * Created by Alexandr Tsvetkov on 10/22/18.
 */
public class LogColor {

    private int background;
    private int foreground;

    public LogColor(int background, int foreground) {
        this.background = background;
        this.foreground = foreground;
    }

    public void setColors(int background, int foreground) {
        this.background = background;
        this.foreground = foreground;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getForeground() {
        return foreground;
    }

    public void setForeground(int foreground) {
        this.foreground = foreground;
    }
}
