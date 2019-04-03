package net.loganford.noideaengine.config;

import lombok.Data;

@Data
public abstract class LoadableConfig {
    @Required private String key;
    private int group = 0;
}
