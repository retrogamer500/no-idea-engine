package net.loganford.noideaengine.utils.file;

import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.utils.glob.Glob;
import net.loganford.noideaengine.utils.glob.GlobActionInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceMapper extends ResourceMapper {

    private ZipFile zipFile;
    private HashMap<String, ZipEntry> entryMap = new HashMap<>();

    public ZipResourceMapper(File file) {
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
    public DataSource get(String resourceKey) {
        if(entryMap.containsKey(resourceKey)) {
            return new ZipDataSource(zipFile, entryMap.get(resourceKey));
        }
        else {
            throw new GameEngineException("Resource does not exist in zip file: " + resourceKey);
        }
    }

    @Override
    public void expandGlob(String glob, GlobActionInterface globAction) {
        Pattern pattern = Glob.globToRegex(glob);
        for(String key : entryMap.keySet()) {
            Matcher matcher = pattern.matcher(key);

            if(matcher.matches()) {
                List<String> groups = new ArrayList<>();
                for(int i = 0; i <= matcher.groupCount(); i++) {
                    groups.add(matcher.group(i));
                }

                globAction.doAction(key, groups);
            }
        }
    }
}
