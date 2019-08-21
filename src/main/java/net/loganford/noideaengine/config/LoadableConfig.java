package net.loganford.noideaengine.config;

import lombok.Data;
import net.loganford.noideaengine.utils.file.ResourceMapper;

@Data
public abstract class LoadableConfig {
    private transient ResourceMapper resourceMapper;
    @Required private String key;
    private int group = 0;
}
