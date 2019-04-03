package net.loganford.noideaengine.utils;

import net.loganford.noideaengine.Game;

public class PerformanceTracker {
    private String name;
    private long nanoseconds;
    private long startTimeNs;
    private long endTimeNs;
    private long maxNs;

    protected PerformanceTracker(String name) {
        this.name = name;
    }

    public void start() {
        startTimeNs = System.nanoTime();
    }

    public void end() {
        endTimeNs = System.nanoTime();
        long difference = endTimeNs - startTimeNs;
        maxNs = Math.max(difference, maxNs);
        nanoseconds += difference;
    }

    public void reset() {
        nanoseconds = 0;
        maxNs = 0;
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    public String getName() {
        return name;
    }

    public double getMaxMs() {
        return (double) maxNs / Game.NANOSECONDS_IN_MILLISECOND;
    }
}
