package net.loganford.noideaengine.resources;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public abstract class Resource {
    @Getter @Setter private int loadingGroup = -1;
    @Getter @Setter private String key;
    @Getter @Setter private Map<String, String> tags = new HashMap<>();
}
