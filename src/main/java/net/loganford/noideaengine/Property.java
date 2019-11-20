package net.loganford.noideaengine;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.resources.Resource;

public class Property extends Resource {
    @Getter @Setter private String stringValue;
    @Getter @Setter private float floatValue;
    @Getter @Setter private int intValue;
    @Getter @Setter private boolean booleanValue;
}
