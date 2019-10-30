package net.loganford.noideaengine.state.entity.components;


import lombok.Getter;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.utils.annotations.Argument;

public abstract class Component {
    @Getter private Entity entity;

    public Component(Argument[] args) {

    }

    public void componentAdded(Entity entity) {
        this.entity = entity;
    }
    public void componentRemoved() {}
}
