package net.loganford.noideaengine.components.physics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;

public class PhysicsComponent extends AbstractPhysicsComponent {

    /**Every time this entity bounces, multiply velocity by this much*/
    @Getter @Setter private float bounceVelocityMultiplier = .3f;
    /**Every time this entity bounces, subtract velocity by this much*/
    @Getter @Setter private float bounceVelocityDampener = .5f;
    /**Every second the entity is sliding on the ground, subtract velocity by this much*/
    @Getter @Setter private float frictionDampener = 60f;
    /**Cosine of an angle. If a slope is smaller than this angle, then no friction occurs.*/
    @Getter @Setter private float rollFactor = 0;
    /**Air resistance-- subtract velocity by this much per second*/
    @Getter @Setter private float drag = 0f;
    /**Max speed per second*/
    @Getter @Setter private float maxSpeed = 500f;

    public PhysicsComponent(Argument[] args) {
        super(args);


        AnnotationUtil.getArgumentOptional("bounceVelocityMultiplier", args).ifPresent((a) -> bounceVelocityMultiplier = a.floatValue());
        AnnotationUtil.getArgumentOptional("bounceVelocityDampener", args).ifPresent((a) -> bounceVelocityDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("frictionDampener", args).ifPresent((a) -> frictionDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("rollFactor", args).ifPresent((a) -> rollFactor = a.floatValue());
        AnnotationUtil.getArgumentOptional("drag", args).ifPresent((a) -> drag = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxSpeed", args).ifPresent((a) -> maxSpeed = a.floatValue());
    }
}
