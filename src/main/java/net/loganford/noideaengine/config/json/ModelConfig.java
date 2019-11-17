package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ModelConfig extends SingleFileConfig {
    private boolean swapZY = false;
    private float scale = 1;
    private String imagePrefix = "";
    private String imageSuffix = "";
}
