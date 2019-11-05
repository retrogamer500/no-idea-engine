package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class CharacterPhysicsComponent extends Component {
    /**Current velocity*/
    @Getter @Setter private Vector3f velocity = new Vector3f();
    /**Class of entities that are solid*/
    @Getter @Setter private Class<?> solidEntity;
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
    /**Gravity per second*/
    @Getter @Setter private Vector3f gravity = new Vector3f(0, 0, 0);
    /**Whether this entity can be pushed by others*/
    @Getter @Setter private boolean interactive = false;
    /**Mass used when calculating collisions. This is a huge approximation so it is better to not use values too different
    * from each other.*/
    @Getter @Setter private float mass = 20f;
    /**Whether to follow character physics.*/
    @Getter @Setter private boolean characterController = false;
    /**Max angle of floor for character physics. If the floor is larger than this angle, then the character will slide
     * off down. Technically this is the arccos of the angle between gravity and the floor normal. Values are between
     * 0 and 1.*/
    @Getter @Setter private float floorAngle = .2f;
    /**Max vertical speed for character physics (along direction of gravity)*/
    @Getter @Setter private float maxVerticalSpeed = 10f;
    /**Max horizontal speed for character physics orthogonal to gravity*/
    @Getter @Setter private float maxHorizontalSpeed = 10f;
    /**Whether the player is on the ground or not*/
    @Getter @Setter private boolean onGround;

    public CharacterPhysicsComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("velocity", args).ifPresent((a) -> AnnotationUtil.set(velocity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("solidEntity", args).ifPresent((a) -> solidEntity = a.classValue());
        AnnotationUtil.getArgumentOptional("bounceVelocityMultiplier", args).ifPresent((a) -> bounceVelocityMultiplier = a.floatValue());
        AnnotationUtil.getArgumentOptional("bounceVelocityDampener", args).ifPresent((a) -> bounceVelocityDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("frictionDampener", args).ifPresent((a) -> frictionDampener = a.floatValue());
        AnnotationUtil.getArgumentOptional("rollFactor", args).ifPresent((a) -> rollFactor = a.floatValue());
        AnnotationUtil.getArgumentOptional("drag", args).ifPresent((a) -> drag = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxSpeed", args).ifPresent((a) -> maxSpeed = a.floatValue());
        AnnotationUtil.getArgumentOptional("gravity", args).ifPresent((a) -> AnnotationUtil.set(gravity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("interactive", args).ifPresent((a) -> interactive = a.booleanValue());
        AnnotationUtil.getArgumentOptional("mass", args).ifPresent((a) -> mass = a.floatValue());
        AnnotationUtil.getArgumentOptional("characterController", args).ifPresent((a) -> characterController = a.booleanValue());
        AnnotationUtil.getArgumentOptional("floorAngle", args).ifPresent((a) -> floorAngle = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxVerticalSpeed", args).ifPresent((a) -> maxVerticalSpeed = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxHorizontalSpeed", args).ifPresent((a) -> maxHorizontalSpeed = a.floatValue());
    }
}
