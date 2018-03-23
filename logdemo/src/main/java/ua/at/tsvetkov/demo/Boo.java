package ua.at.tsvetkov.demo;

import ua.at.tsvetkov.annotations.ToLog;

/**
 * Created by Alexandr Tsvetkov on 3/23/2018.
 */

public class Boo {

    private final boolean isNeed;

    @ToLog
    public Boo(boolean isNeed) {
        this.isNeed = isNeed;
    }

    @ToLog
    public boolean isNeed() {
        return isNeed;
    }

}
