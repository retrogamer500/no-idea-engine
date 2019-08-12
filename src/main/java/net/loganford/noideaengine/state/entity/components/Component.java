package net.loganford.noideaengine.state.entity.components;


import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.entity.Entity;

public abstract class Component {
    @Getter @Setter private Entity entity;
}
