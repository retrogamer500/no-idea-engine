package net.loganford.noideaengine.config.json;

import lombok.Data;
import net.loganford.noideaengine.utils.file.ResourceMapper;
import net.loganford.noideaengine.utils.json.Required;

@Data
public abstract class LoadableConfig {
    private transient ResourceMapper resourceMapper;
    @Required
    private String key;
    private int group = 0;
}
