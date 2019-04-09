package net.loganford.noideaengine.utils.file;

import java.io.IOException;
import java.io.InputStream;

public class JarResourceLocation extends ResourceLocation {
    private ClassLoader classLoader;
    private String resource;

    public JarResourceLocation(ClassLoader classLoader, String resource) {
        this.classLoader = classLoader;
        this.resource = resource;
    }

    @Override
    protected InputStream getInputStream() {
        return classLoader.getResourceAsStream(resource);
    }

    @Override
    public String toString() {
        return "jar://" + resource;
    }

    @Override
    public boolean exists() {
        try {
            InputStream inputStream = getInputStream();
            boolean returnValue = inputStream != null;
            if(inputStream != null) {
                inputStream.close();
            }
            return returnValue;
        }
        catch(IOException e) {
            return false;
        }
    }
}
