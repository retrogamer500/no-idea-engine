package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.LoadableConfig;

@Data
@EqualsAndHashCode(callSuper=true)
public class ImageConfig extends LoadableConfig {
    private String filename;
    private boolean flipVertically;
}
