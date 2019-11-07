package net.loganford.noideaengine.utils.annotations;

import net.loganford.noideaengine.systems.EntitySystem;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(RegisterSystem.List.class)
public @interface RegisterSystem {
    Class<? extends EntitySystem> value();
    Argument[] arguments() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        RegisterSystem[] value();
    }
}
