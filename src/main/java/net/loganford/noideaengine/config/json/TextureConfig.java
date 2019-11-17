package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TextureConfig extends SingleFileConfig {
    private boolean flipVertically;
}