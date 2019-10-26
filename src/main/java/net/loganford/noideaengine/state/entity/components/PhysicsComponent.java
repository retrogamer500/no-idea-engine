package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector3f;

public class PhysicsComponent extends Component {
    /*Current velocity*/
    @Getter @Setter private Vector3f velocity = new Vector3f();
    /*Class of entities that are solid*/
    @Getter @Setter private Class<? extends Entity> solidEntity;
    /*If velocity projected on the normal is greater than this, then bounce, otherwise slide*/
    @Getter @Setter private float bounceVelocity = .1f;
    /*Every time this entity bounces, multiply velocity by this much*/
    @Getter @Setter private float bounceVelocityMultiplier = .8f;
    /*Every time this entity bounces, subtract velocity by this much*/
    @Getter @Setter private float bounceVelocityDampener = .1f;
    /*Friction per second*/
    @Getter @Setter private float friction = 32f;
    /*Max speed*/
    @Getter @Setter private float maxSpeed = 500f;
    /*Gravity*/
    @Getter @Setter private Vector3f gravity = new Vector3f(0, 0, 0);
}
