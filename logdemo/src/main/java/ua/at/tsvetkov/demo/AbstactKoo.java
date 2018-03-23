package ua.at.tsvetkov.demo;

import ua.at.tsvetkov.annotations.ToLog;

/**
 * Created by Alexandr Tsvetkov on 3/23/2018.
 */

public abstract class AbstactKoo {

    protected boolean isNeed;

    @ToLog
    public AbstactKoo(boolean isNeed) {
        this.isNeed = isNeed;
    }

    @ToLog
    public abstract boolean isNeed();

}
