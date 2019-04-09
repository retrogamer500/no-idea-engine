package net.loganford.noideaengine.alarm;

import lombok.Getter;
import lombok.Setter;

public class Alarm {
    private Action action;

    @Getter private long counter = 0;

    @Getter @Setter private double frequency;
    @Getter @Setter private boolean active = true;
    @Getter @Setter private boolean repeatable;

    public Alarm(double frequency, boolean repeatable, Action action) {
        this.frequency = frequency;
        this.repeatable = repeatable;
        this.action = action;
    }

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
