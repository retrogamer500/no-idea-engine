package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.GlobField;
import net.loganford.noideaengine.config.GlobType;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleFileConfig extends LoadableConfig {
    @GlobField(GlobType.FILE)
    private String filename;
}
