package net.loganford.noideaengine.utils.annotations;

import net.loganford.noideaengine.components.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(UnregisterComponent.List.class)
public @interface UnregisterComponent {
    Class<? extends Component> value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        UnregisterComponent[] value();
    }
}
