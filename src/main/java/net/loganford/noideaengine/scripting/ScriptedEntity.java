package net.loganford.noideaengine.scripting;

import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.entity.Entity;

public class ScriptedEntity extends Resource {
    private Class<? extends Entity> clazz;

    public ScriptedEntity(Class<? extends Entity> clazz) {
        this.clazz = clazz;
    }

    public Entity newInstance() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new GameEngineException(e);
        }
    }
}
