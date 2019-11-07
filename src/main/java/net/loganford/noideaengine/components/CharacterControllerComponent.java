package net.loganford.noideaengine.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class CharacterControllerComponent extends Component {
    @Getter @Setter private Vector3f velocity = new Vector3f();
    @Getter @Setter private float acceleration = 30f;
    @Getter @Setter private float jumpSpeed = 30f;

    public CharacterControllerComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("acceleration", args).ifPresent((a) -> acceleration = a.floatValue());
        AnnotationUtil.getArgumentOptional("jumpSpeed", args).ifPresent((a) -> jumpSpeed = a.floatValue());
    }
}
