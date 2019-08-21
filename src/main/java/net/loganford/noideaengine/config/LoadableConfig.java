package net.loganford.noideaengine.config;

import lombok.Data;
import net.loganford.noideaengine.utils.file.AbstractResourceMapper;

@Data
public abstract class LoadableConfig {
    private transient AbstractResourceMapper resourceMapper;
    @Required private String key;
    private int group = 0;
}
