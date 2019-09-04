package net.loganford.noideaengine.state.entity.systems;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(UnregisterSystem.List.class)
public @interface UnregisterSystem {
    Class<? extends EntitySystem> value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        UnregisterSystem[] value();
    }
}
