package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.SingleFileConfig;

@Data
@EqualsAndHashCode(callSuper=true)
public class ImageConfig extends SingleFileConfig {
    private boolean flipVertically;
}
