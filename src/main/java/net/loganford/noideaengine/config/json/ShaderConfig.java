package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.LoadableConfig;
import net.loganford.noideaengine.config.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class ShaderConfig extends LoadableConfig {
    @Required public String vert;
    @Required public String frag;
}
