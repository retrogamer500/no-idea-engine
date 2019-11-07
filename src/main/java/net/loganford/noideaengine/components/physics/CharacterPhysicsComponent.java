package net.loganford.noideaengine.components.physics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;

public class CharacterPhysicsComponent extends AbstractPhysicsComponent {
    /**Every second the entity is sliding on the ground, subtract velocity by this much*/
    @Getter @Setter private float friction = 60f;
    /**Air resistance-- subtract velocity by this much per second*/
    @Getter @Setter private float drag = 0f;
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
    @Getter @Setter private boolean onGroundLast;

    public CharacterPhysicsComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("drag", args).ifPresent((a) -> drag = a.floatValue());
        AnnotationUtil.getArgumentOptional("floorAngle", args).ifPresent((a) -> floorAngle = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxVerticalSpeed", args).ifPresent((a) -> maxVerticalSpeed = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxHorizontalSpeed", args).ifPresent((a) -> maxHorizontalSpeed = a.floatValue());
    }
}
