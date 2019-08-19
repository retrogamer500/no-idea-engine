package net.loganford.noideaengine.utils.file;

import java.io.File;

public class FileResourceMapper extends AbstractResourceMapper {
    private File folder;

    public FileResourceMapper(File folder) {
        this.folder = folder;
    }

    @Override
    public AbstractResource get(String resourceKey) {
        return new FileResource(folder, resourceKey);
    }
}
