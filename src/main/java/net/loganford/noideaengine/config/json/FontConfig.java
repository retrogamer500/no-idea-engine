package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class FontConfig extends SingleFileConfig {
    @Required
    private float size;
}
