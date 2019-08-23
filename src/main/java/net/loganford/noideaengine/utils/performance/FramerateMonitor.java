package net.loganford.noideaengine.utils.performance;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FramerateMonitor {

    public static final long UPDATE_INTERVAL = Game.NANOSECONDS_IN_SECOND;

    private int framesPerSecond;
    private int frameCounter;
    private long timeCounter;
    private List<PerformanceTracker> trackers = new ArrayList<>();

    public void start() {
        frameCounter = 0;
        timeCounter = System.nanoTime();
    }

    public void update() {
        long currentTime = System.nanoTime();
        if (currentTime - timeCounter > UPDATE_INTERVAL) {
            // Update FPS
            framesPerSecond = frameCounter;
            frameCounter = 0;
            timeCounter += UPDATE_INTERVAL;

            log.debug("Frames per second: " + framesPerSecond);

            // Calculate breakdowns

            for (PerformanceTracker tracker : trackers) {
                double millisecondPerFrame = (tracker.getNanoseconds()
                        / ((double) Game.NANOSECONDS_IN_MILLISECOND * framesPerSecond));
                log.debug("Task: " + tracker.getName() + " | Avg Time: " + millisecondPerFrame + "ms" + " | Max Time: " + tracker.getMaxMs() + "ms");

                tracker.reset();
            }
            log.debug("");
        }
        frameCounter++;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public PerformanceTracker getPerformanceTracker(String name) {
        PerformanceTracker tracker = new PerformanceTracker(name);
        trackers.add(tracker);
        return tracker;
    }
}
