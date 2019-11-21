package net.loganford.noideaengine;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.resources.Resource;

public class Property extends Resource {
    @Setter private String stringValue;
    @Getter @Setter private Float floatValue;
    @Getter @Setter private Integer intValue;
    @Getter @Setter private Boolean booleanValue;

    public String getStringValue() {
        if(stringValue != null) {
            return stringValue;
        }
        if(floatValue != null) {
            return floatValue.toString();
        }
        if(intValue != null) {
            return intValue.toString();
        }
        if(booleanValue != null) {
            return booleanValue.toString();
        }
        return "";
    }
}
