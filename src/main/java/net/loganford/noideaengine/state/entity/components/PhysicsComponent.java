package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector3f;

public class PhysicsComponent extends Component {
    /**Current velocity*/
    @Getter @Setter private Vector3f velocity = new Vector3f();
    /**Class of entities that are solid*/
    @Getter @Setter private Class<? extends Entity> solidEntity;
    /**Every time this entity bounces, multiply velocity by this much*/
    @Getter @Setter private float bounceVelocityMultiplier = .3f;
    /**Every time this entity bounces, subtract velocity by this much*/
    @Getter @Setter private float bounceVelocityDampener = .5f;
    /**Every second the entity is sliding on the ground, subtract velocity by this much*/
    @Getter @Setter private float frictionDampener = 5f;
    /**Cosine of an angle. If a slope is smaller than this angle, then no friction occurs.*/
    @Getter @Setter private float rollFactor = 0;
    /**Air resistance-- subtract velocity by this much per second*/
    @Getter @Setter private float resistance = 0f;
    /**Max speed per second*/
    @Getter @Setter private float maxSpeed = 500f;
    /**Gravity per second*/
    @Getter @Setter private Vector3f gravity = new Vector3f(0, 0, 0);

    public PhysicsComponent(String[] args) {
        super(args);
    }
}
