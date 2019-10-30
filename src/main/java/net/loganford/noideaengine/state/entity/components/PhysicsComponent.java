package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class PhysicsComponent extends Component {
    /**Current velocity*/
    @Getter @Setter private Vector3f velocity = new Vector3f();
    /**Class of entities that are solid*/
    @Getter @Setter private Class<?> solidEntity;
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
    /*Whether this entity can be pushed by others*/
    @Getter @Setter private boolean interactive = false;
    /*Mass used when calculating collisions. This is a huge approximation so it is better to not use values too different
    * from each other.*/
    @Getter @Setter private float mass = 20f;

    public PhysicsComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("velocity", args).ifPresent((a) -> AnnotationUtil.set(velocity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("solidEntity", args).ifPresent((a) -> solidEntity = a.classValue());
        AnnotationUtil.getArgumentOptional("bounceVelocityMultiplier", args).ifPresent((a) -> bounceVelocityMultiplier = a.floatValue());
        AnnotationUtil.getArgumentOptional("bounceVelocityDampener", args).ifPresent((a) -> bounceVelocityDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("frictionDampener", args).ifPresent((a) -> frictionDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("rollFactor", args).ifPresent((a) -> rollFactor = a.floatValue());
        AnnotationUtil.getArgumentOptional("resistance", args).ifPresent((a) -> resistance = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxSpeed", args).ifPresent((a) -> maxSpeed = a.floatValue());
        AnnotationUtil.getArgumentOptional("gravity", args).ifPresent((a) -> AnnotationUtil.set(gravity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("interactive", args).ifPresent((a) -> interactive = a.booleanValue());
        AnnotationUtil.getArgumentOptional("mass", args).ifPresent((a) -> mass = a.floatValue());
    }
}
