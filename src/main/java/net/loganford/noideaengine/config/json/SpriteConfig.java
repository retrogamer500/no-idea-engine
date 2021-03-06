package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.GlobField;
import net.loganford.noideaengine.config.GlobType;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class SpriteConfig extends LoadableConfig {

    @Required
    @GlobField(GlobType.IMAGE_KEY)
    private String imageKey;
    private Float frameWidth;
    private Float frameHeight;
    private Integer padding;
    private Integer length;
    private Float duration;
    private float offsetX = 0;
    private float offsetY = 0;
}
