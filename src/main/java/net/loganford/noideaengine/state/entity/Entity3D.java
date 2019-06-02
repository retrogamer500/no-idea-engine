package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.Scene;
import org.joml.Vector3f;

public abstract class Entity3D<G extends Game, T extends Scene<G>> extends AbstractEntity<G, T> {
    @Getter @Setter protected Vector3f position = new Vector3f(0f, 0f, 0f);
}
