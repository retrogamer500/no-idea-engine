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
    /**Max angle of floor for character physics. 0 = flat, PI/2 = vertical*/
    @Getter @Setter private float floorAngle = (float) Math.PI/4;
    /**Max vertical speed for character physics (along direction of gravity)*/
    @Getter @Setter private float maxVerticalSpeed = 10f;
    /**Max horizontal speed for character physics orthogonal to gravity*/
    @Getter @Setter private float maxHorizontalSpeed = 10f;
    /**Whether the player is on the ground or not*/
    @Getter @Setter private boolean onGround;

    public CharacterPhysicsComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("friction", args).ifPresent((a) -> friction = a.floatValue());
        AnnotationUtil.getArgumentOptional("drag", args).ifPresent((a) -> drag = a.floatValue());
        AnnotationUtil.getArgumentOptional("floorAngle", args).ifPresent((a) -> floorAngle = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxVerticalSpeed", args).ifPresent((a) -> maxVerticalSpeed = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxHorizontalSpeed", args).ifPresent((a) -> maxHorizontalSpeed = a.floatValue());
    }
}
