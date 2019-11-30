package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
public class PropertyConfig extends LoadableConfig {
    @Getter @Setter private String stringValue;
    @Getter @Setter private float floatValue;
    @Getter @Setter private int intValue;
    @Getter @Setter private boolean booleanValue;
    @Getter @Setter private String filename;
}
