package net.loganford.noideaengine.utils.file;

import java.io.InputStream;

public class JarDataSource extends DataSource {
    private ClassLoader classLoader;
    private String resource;

    public JarDataSource(ClassLoader classLoader, String resource) {
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
