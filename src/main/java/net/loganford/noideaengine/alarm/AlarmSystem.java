package net.loganford.noideaengine.alarm;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class AlarmSystem {
    private List<Alarm> alarms = new ArrayList<>();

    public void add(Alarm alarm) {
        log.info("Adding alarm!");
        alarms.add(alarm);
    }

    public void add(long frequency, boolean repeatable, Action action) {
        Alarm alarm = new Alarm(frequency, repeatable, action);
        add(alarm);
    }

    public void add(long frequency, Action action) {
        add(frequency, false, action);
    }

    public void step(float delta) {
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
