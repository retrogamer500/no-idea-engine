package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AbstractCameraComponent extends Component {
    private static Vector3f V3F = new Vector3f();

    @Getter @Setter private float sensitivity = .001f;
    @Getter @Setter private float pitch;
    @Getter @Setter private float roll;
    @Getter @Setter private float yaw;

    public Vector3fc getDirection() {
        V3F.set(1, 0, 0);
        V3F.rotateX(getRoll());
        V3F.rotateZ(-getPitch());
        V3F.rotateY(-getYaw());
        return V3F;
    }
}
