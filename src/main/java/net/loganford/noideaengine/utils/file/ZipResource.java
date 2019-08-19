package net.loganford.noideaengine.utils.file;

import net.loganford.noideaengine.GameEngineException;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResource extends AbstractResource {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    public ZipResource(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    @Override
    protected InputStream getInputStream() {
        try {
            return zipFile.getInputStream(zipEntry);
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    public String toString() {
        return "zip://" + zipEntry.getName();
    }

    @Override
    public boolean exists() {
        return zipEntry != null;
    }
}
