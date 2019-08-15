package net.loganford.noideaengine.alarm;

import lombok.Getter;
import lombok.Setter;

public class Alarm {
    private Action action;

    @Getter private long counter = 0;

    @Getter @Setter private double frequency;
    @Getter @Setter private boolean active = true;
    @Getter @Setter private boolean repeatable;

    /**
     * Creates an alarm.
     * @param frequency how long until the alarm executes an action
     * @param repeatable whether the alarm repeats
     * @param action the action to perform
     */
    public Alarm(double frequency, boolean repeatable, Action action) {
        this.frequency = frequency;
        this.repeatable = repeatable;
        this.action = action;
    }

    /**
     * Steps the alarm by a certain amount
     * @param delta
     */
    public void step(float delta) {
        counter+= delta;
        while(counter > frequency) {
            action.doAction();

            if(!repeatable) {
                active = false;
                break;
            }
            else {
                counter -= frequency;
            }
        }
    }
}
