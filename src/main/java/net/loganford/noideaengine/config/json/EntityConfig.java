package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.GlobField;
import net.loganford.noideaengine.config.GlobType;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class EntityConfig extends LoadableConfig {
    @Required
    @GlobField(GlobType.SCRIPT_KEY)
    private String scriptKey;
    private String function = "getClass";
}
