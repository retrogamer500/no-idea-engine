package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.utils.json.Required;

@Data
@EqualsAndHashCode(callSuper=true)
public class CubeMapConfig extends LoadableConfig {
    @Required
    private String backFilename;
    @Required
    private String downFilename;
    @Required
    private String frontFilename;
    @Required
    private String leftFilename;
    @Required
    private String rightFilename;
    @Required
    private String upFilename;
}
