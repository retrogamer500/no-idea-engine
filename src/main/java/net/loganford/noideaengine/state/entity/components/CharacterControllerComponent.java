package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public class CharacterControllerComponent extends Component {
    @Getter @Setter private Vector3f velocity = new Vector3f();
    @Getter @Setter private float acceleration = 30f;
    @Getter @Setter private float jumpSpeed = 30f;

    public CharacterControllerComponent(Argument[] args) {
        super(args);
    }
}
