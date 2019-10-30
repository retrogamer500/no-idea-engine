package net.loganford.noideaengine.utils.annotations;

import org.joml.Vector3f;

import java.util.Optional;

public class AnnotationUtil {
    public static Optional<Argument> getArgumentOptional(String name, Argument[] arguments) {
        return Optional.ofNullable(getArgument(name, arguments));
    }

    public static Argument getArgument(String name, Argument[] arguments) {
        Argument optionalArgument = null;
        for(Argument argument : arguments) {
            if(argument.name().equals(name)) {
                optionalArgument = argument;
                break;
            }
        }
        return optionalArgument;
    }

    public static void set(Vector3f vector, Vector3fa vectorAnnotation) {
        vector.x = vectorAnnotation.x();
        vector.y = vectorAnnotation.y();
        vector.z = vectorAnnotation.z();
    }
}
