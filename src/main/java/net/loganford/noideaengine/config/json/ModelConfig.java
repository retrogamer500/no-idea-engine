package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class ModelConfig extends LoadableConfig {
    @Required
    private String filename;
    private boolean swapZY = false;
    private float scale = 1;
}
