package net.loganford.noideaengine.utils.file;

import lombok.Getter;
import net.loganford.noideaengine.GameEngineException;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileDataSource extends DataSource {

    @Getter private File file;

    public FileDataSource(File fileLocation) {
        file = fileLocation;
    }

    public FileDataSource(File folder, String location) {
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

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    @Override
    public void save(String data) {
        try {
            FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    @Override
    public boolean exists() {
        return file.exists();
    }
}
