package net.loganford.noideaengine.alarm;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class AlarmSystem {
    private List<Alarm> alarms = new ArrayList<>();

    /**
     * Adds an alarm to the system
     * @param alarm the alarm to add
     */
    public void add(Alarm alarm) {
        log.info("Adding alarm!");
        alarms.add(alarm);
    }

    /**
     * Creates an alarm and adds it to the system
     * @param frequency how long until the alarm executes an action
     * @param repeatable whether the alarm repeats
     * @param action the action to perform
     */
    public void add(long frequency, boolean repeatable, Action action) {
        Alarm alarm = new Alarm(frequency, repeatable, action);
        add(alarm);
    }

    /**
     * Creates an alarm and adds it to the system
     * @param frequency how long until the alarm executes an action
     * @param action the action to perform
     */
    public void add(long frequency, Action action) {
        add(frequency, false, action);
    }

    /**
     * Steps all the alarms controlled by the system
     * @param delta
     */
    public void step(float delta) {
        //Does an iterator every frame per object put too much pressure on the GC?
        Iterator<Alarm> iter = alarms.iterator();
        while(iter.hasNext()) {
            Alarm alarm = iter.next();
            alarm.step(delta);

            if(!alarm.isActive()) {
                log.info("Removing alarm!");
                iter.remove();
            }
        }
    }
}
