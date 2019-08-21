package net.loganford.noideaengine.config.json;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleFileConfig extends LoadableConfig implements Cloneable {
    private String filename;

    public SingleFileConfig clone() throws CloneNotSupportedException {
        return (SingleFileConfig) super.clone();
    }
}
