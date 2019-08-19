package net.loganford.noideaengine.utils.file;

public class JarResourceMapper extends AbstractResourceMapper {

    private ClassLoader classLoader;

    public JarResourceMapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public AbstractResource get(String resourceKey) {
        return new JarResource(classLoader, resourceKey);
    }
}
