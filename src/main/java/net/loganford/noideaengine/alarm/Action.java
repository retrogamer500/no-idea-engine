package net.loganford.noideaengine.alarm;

/**
 * An action to perform after an alarm is triggered
 */
public interface Action {
    /**
     * Perform a user-specified action
     */
    void doAction();
}
