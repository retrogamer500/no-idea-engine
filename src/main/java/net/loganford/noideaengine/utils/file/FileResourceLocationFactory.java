package net.loganford.noideaengine.utils.file;

import java.io.File;

public class FileResourceLocationFactory extends ResourceLocationFactory {
    private File folder;

    public FileResourceLocationFactory(File folder) {
        this.folder = folder;
    }

    @Override
    public ResourceLocation get(String resourceLocation) {
        return new FileResourceLocation(folder, resourceLocation);
    }
}
