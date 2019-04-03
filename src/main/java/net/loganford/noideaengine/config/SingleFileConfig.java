package net.loganford.noideaengine.config;

import lombok.Data;

@Data
public class SingleFileConfig extends LoadableConfig {
    private String filename;
}
