package net.loganford.noideaengine.utils.file;

public class JarResourceLocationFactory extends ResourceLocationFactory {

    private ClassLoader classLoader;

    public JarResourceLocationFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ResourceLocation get(String resourceLocation) {
        return new JarResourceLocation(classLoader, resourceLocation);
    }
}
