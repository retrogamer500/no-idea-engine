package net.loganford.noideaengine.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class CharacterControllerComponent extends Component {
    @Getter @Setter private Vector3f velocity = new Vector3f();
    @Getter @Setter private float acceleration = 30f;
    @Getter @Setter private float jumpSpeed = 30f;

    @Getter @Setter private int upKey = Input.KEY_W;
    @Getter @Setter private int downKey = Input.KEY_S;
    @Getter @Setter private int leftKey = Input.KEY_A;
    @Getter @Setter private int rightKey = Input.KEY_D;
    @Getter @Setter private int jumpKey = Input.KEY_SPACE;

    public CharacterControllerComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("acceleration", args).ifPresent((a) -> acceleration = a.floatValue());
        AnnotationUtil.getArgumentOptional("jumpSpeed", args).ifPresent((a) -> jumpSpeed = a.floatValue());

        AnnotationUtil.getArgumentOptional("upKey", args).ifPresent((a) -> upKey = a.intValue());
        AnnotationUtil.getArgumentOptional("downKey", args).ifPresent((a) -> downKey = a.intValue());
        AnnotationUtil.getArgumentOptional("leftKey", args).ifPresent((a) -> leftKey = a.intValue());
        AnnotationUtil.getArgumentOptional("rightKey", args).ifPresent((a) -> rightKey = a.intValue());
        AnnotationUtil.getArgumentOptional("jumpKey", args).ifPresent((a) -> jumpKey = a.intValue());
    }
}
