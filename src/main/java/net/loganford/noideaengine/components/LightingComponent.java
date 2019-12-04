package net.loganford.noideaengine.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.lighting.Light;
import net.loganford.noideaengine.utils.annotations.Argument;

public class LightingComponent extends Component {
    @Getter @Setter private Light light;

    public LightingComponent(Argument[] args) {
        super(args);
    }
}
