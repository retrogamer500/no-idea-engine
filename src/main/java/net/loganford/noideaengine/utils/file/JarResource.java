package net.loganford.noideaengine.utils.file;

import java.io.InputStream;

public class JarResource extends AbstractResource {
    private ClassLoader classLoader;
    private String resource;

    public JarResource(ClassLoader classLoader, String resource) {
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
        return classLoader.getResource(resource) != null;
    }
}
