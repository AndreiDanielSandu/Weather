package sysint;

public class SystemClock implements Clock {
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}