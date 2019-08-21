package net.loganford.noideaengine.utils.file;

public class JarResourceMapper extends ResourceMapper {

    private ClassLoader classLoader;

    public JarResourceMapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public DataSource get(String resourceKey) {
        return new JarDataSource(classLoader, resourceKey);
    }
}
