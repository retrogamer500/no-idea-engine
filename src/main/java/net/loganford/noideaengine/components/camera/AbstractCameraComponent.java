package net.loganford.noideaengine.components.camera;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AbstractCameraComponent extends Component {
    private static Vector3f V3F = new Vector3f();

    @Getter @Setter private float sensitivity = .001f;
    @Getter @Setter private float pitch;
    @Getter @Setter private float roll;
    @Getter @Setter private float yaw;
    @Getter @Setter private float minPitch = (float) (-Math.PI/2f * .99f);
    @Getter @Setter private float maxPitch = (float) (Math.PI/2f * .99f);

    public AbstractCameraComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("sensitivity", args).ifPresent((a) -> sensitivity = a.floatValue());
        AnnotationUtil.getArgumentOptional("pitch", args).ifPresent((a) -> pitch = a.floatValue());
        AnnotationUtil.getArgumentOptional("roll", args).ifPresent((a) -> roll = a.floatValue());
        AnnotationUtil.getArgumentOptional("yaw", args).ifPresent((a) -> yaw = a.floatValue());
        AnnotationUtil.getArgumentOptional("minPitch", args).ifPresent((a) -> minPitch = a.floatValue());
        AnnotationUtil.getArgumentOptional("maxPitch", args).ifPresent((a) -> maxPitch = a.floatValue());
    }

    public Vector3fc getDirection() {
        V3F.set(1, 0, 0);
        V3F.rotateX(getRoll());
        V3F.rotateZ(-getPitch());
        V3F.rotateY(-getYaw());
        return V3F;
    }
}
