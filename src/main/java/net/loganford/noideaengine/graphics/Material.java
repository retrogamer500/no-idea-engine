package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;

public class Material implements Comparable<Material>  {
    @Getter @Setter private Texture diffuse;


    @Override
    public int compareTo(Material o) {
        if(o.getDiffuse().getTextureId() < getDiffuse().getTextureId()) {
            return -1;
        }
        if(o.getDiffuse().getTextureId() > getDiffuse().getTextureId()) {
            return 1;
        }

        return 0;
    }
}
