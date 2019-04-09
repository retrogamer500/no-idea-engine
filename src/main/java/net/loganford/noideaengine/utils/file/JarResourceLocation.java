package net.loganford.noideaengine.utils.file;

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
}
