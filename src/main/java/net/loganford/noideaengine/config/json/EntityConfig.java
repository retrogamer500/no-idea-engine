package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class EntityConfig extends LoadableConfig {
    @Required
    private String scriptKey;
    private String function = "getClass";
}
