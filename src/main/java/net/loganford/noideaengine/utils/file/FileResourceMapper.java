package net.loganford.noideaengine.utils.file;

import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.utils.glob.Glob;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileResourceMapper extends ResourceMapper {
    private File folder;

    public FileResourceMapper(File folder) {
        this.folder = folder;
    }

    @Override
    public DataSource get(String resourceKey) {
        return new FileDataSource(folder, resourceKey);
    }

    @Override
    public void expandGlob(String glob, GlobActionInterface globAction) {
        try {
            Path basePath = folder.toPath();
            Pattern pattern = Glob.globToRegex(glob);
            Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    String resourceKey = basePath.relativize(path).toString();
                    resourceKey = resourceKey.replace("\\", "/");
                    Matcher matcher = pattern.matcher(resourceKey);
                    if(matcher.matches()) {
                        List<String> groups = new ArrayList<>();
                        for(int i = 0; i <= matcher.groupCount(); i++) {
                            groups.add(matcher.group(i));
                        }

                        globAction.doAction(resourceKey, groups);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch(Exception e){
            throw new GameEngineException(e);
        }
    }
}