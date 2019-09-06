package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.loganford.noideaengine.config.GlobType;
import net.loganford.noideaengine.config.GlobableField;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleFileConfig extends LoadableConfig {
    @GlobableField(GlobType.FILE)
    private String filename;
}
