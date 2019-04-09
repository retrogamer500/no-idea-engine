package net.loganford.noideaengine.utils.file;

import net.loganford.noideaengine.GameEngineException;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileResourceLocationFactory extends ResourceLocationFactory {

    private ZipFile zipFile;
    private HashMap<String, ZipEntry> entryMap = new HashMap<>();

    public ZipFileResourceLocationFactory(File file) {
        try {
            zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                entryMap.put(entry.getName(), entry);
            }
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    @Override
    public ResourceLocation get(String resourceLocation) {
        if(entryMap.containsKey(resourceLocation)) {
            return new ZipFileResourceLocation(zipFile, entryMap.get(resourceLocation));
        }
        else {
            throw new GameEngineException("Resource does not exist in zip file: " + resourceLocation);
        }
    }
}
