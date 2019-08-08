package net.loganford.noideaengine.state.entity.systems;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(RegisterSystem.List.class)
public @interface RegisterSystem {
    Class<AbstractEntitySystem> clazz();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        RegisterSystem[] value();
    }
}
