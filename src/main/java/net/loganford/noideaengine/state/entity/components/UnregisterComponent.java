package net.loganford.noideaengine.state.entity.components;

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
