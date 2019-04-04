package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.LoadableConfig;
import net.loganford.noideaengine.config.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class SpriteConfig extends LoadableConfig {

    @Required private String imageKey;
    private Float frameWidth;
    private Float frameHeight;
    private Integer padding;
    private Integer length;
    private Float duration;
    private float offsetX = 0;
    private float offsetY = 0;
}
