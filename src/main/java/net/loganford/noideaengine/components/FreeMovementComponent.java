package net.loganford.noideaengine.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class FreeMovementComponent extends Component {
    @Getter @Setter private Vector3f velocity = new Vector3f();
    @Getter @Setter private float acceleration = 2f;
    @Getter @Setter private float friction = 1f;
    @Getter @Setter private float maxSpeed = 10f;

    public FreeMovementComponent(Argument[] args) {
        super(args);
    }
}
