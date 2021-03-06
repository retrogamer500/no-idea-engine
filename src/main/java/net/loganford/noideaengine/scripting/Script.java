package net.loganford.noideaengine.scripting;

import lombok.Getter;
import net.loganford.noideaengine.resources.Resource;


public abstract class Script extends Resource {
    @Getter
    protected boolean executed = false;

    public abstract void execute();
    public abstract float getFloat(String key);
    public abstract float getInt(String key);
    public abstract String getString(String key);
    public abstract <C> C getObject(String key, Class<C> clazz);
    public abstract Function getFunction(String key);
}
