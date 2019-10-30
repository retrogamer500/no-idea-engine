package net.loganford.noideaengine.utils.annotations;

import java.util.Optional;

public class AnnotationUtil {
    public static Optional<Argument> getArgumentOptional(String name, Argument[] arguments) {
        return Optional.of(getArgument(name, arguments));
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
}
