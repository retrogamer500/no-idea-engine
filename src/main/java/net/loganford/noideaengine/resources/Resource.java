package net.loganford.noideaengine.resources;

import lombok.Getter;
import lombok.Setter;

public abstract class Resource {
    @Getter @Setter private int loadingGroup = -1;
    @Getter @Setter private String key;
}
