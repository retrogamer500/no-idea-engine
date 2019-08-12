package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.shape.Shape2D;

public class BasicCollisionComponent extends CollisionComponent {
    @Getter @Setter private Shape2D mask;
}
