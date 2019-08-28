package net.loganford.noideaengine.scripting;

import net.loganford.noideaengine.resources.Resource;

import java.util.Map;


public abstract class Script extends Resource {

    public abstract void execute(Map<String, Object> context, Map<String, Object> output);

    public void execute(Map<String, Object> output) {
        execute(null, output);
    }

    public void execute() {
        execute(null, null);
    }
}
