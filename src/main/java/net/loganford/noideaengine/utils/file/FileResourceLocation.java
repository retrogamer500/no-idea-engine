package net.loganford.noideaengine.utils.file;

import lombok.Getter;
import net.loganford.noideaengine.GameEngineException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResourceLocation extends ResourceLocation {

    @Getter private File file;

    public FileResourceLocation(File fileLocation) {
        file = fileLocation;
    }

    public FileResourceLocation(File folder, String location) {
        if(folder.isFile()) {
            throw new GameEngineException("Folder " + folder.getAbsolutePath() + " must be a valid directory.");
        }

        file = new File(folder.getAbsolutePath() + "/" + location);
    }

    @Override
    protected InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        }
        catch(FileNotFoundException e) {
            throw new GameEngineException(e);
        }
    }

    @Override
    public String toString() {
        return "file://" + file.toString();
    }
}
