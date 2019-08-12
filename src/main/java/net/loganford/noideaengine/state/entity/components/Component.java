package net.loganford.noideaengine.state.entity.components;


import lombok.Getter;
import net.loganford.noideaengine.state.entity.Entity;

public abstract class Component {
    @Getter private Entity entity;

    public void init(Entity entity) {
        this.entity = entity;
    }
}
