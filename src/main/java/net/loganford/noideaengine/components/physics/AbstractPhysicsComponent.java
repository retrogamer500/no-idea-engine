package net.loganford.noideaengine.components.physics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3f;

public abstract class AbstractPhysicsComponent extends Component {
    /**Current velocity*/
    @Getter @Setter private Vector3f velocity = new Vector3f();
    /**Class of entities that are solid*/
    @Getter @Setter private Class<?> solidEntity;
    /**Gravity per second*/
    @Getter @Setter private Vector3f gravity = new Vector3f(0, 0, 0);
    /**Whether this entity can be pushed by others*/
    @Getter @Setter private boolean interactive = false;
    /**Mass used when calculating collisions. This is a huge approximation so it is better to not use values too different
    * from each other.*/
    @Getter @Setter private float mass = 20f;

    public AbstractPhysicsComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("velocity", args).ifPresent((a) -> AnnotationUtil.set(velocity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("solidEntity", args).ifPresent((a) -> solidEntity = a.classValue());
        AnnotationUtil.getArgumentOptional("gravity", args).ifPresent((a) -> AnnotationUtil.set(gravity, a.vectorValue()));
        AnnotationUtil.getArgumentOptional("interactive", args).ifPresent((a) -> interactive = a.booleanValue());
        AnnotationUtil.getArgumentOptional("mass", args).ifPresent((a) -> mass = a.floatValue());
    }
}
