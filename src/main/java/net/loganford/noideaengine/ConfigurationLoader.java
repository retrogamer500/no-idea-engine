package net.loganford.noideaengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.config.SingleFileConfig;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.config.json.ImageConfig;
import net.loganford.noideaengine.config.json.Resources;
import net.loganford.noideaengine.utils.JsonValidator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ConfigurationLoader {
    public static final String DEFAULT_CONFIG_LOCATION = "game.json";

    @Getter @Setter private String imageScanDirectory = null;
    @Getter @Setter private String configLocation = DEFAULT_CONFIG_LOCATION;

    public GameConfig loadConfiguration() {
        GameConfig config;
        boolean configDirty = false;

        File configFile = new File(configLocation);
        if(configFile.exists()) {
            log.info("Loading configuration file: " + configLocation);
            Gson gson = new Gson();

            try {
                String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                config = gson.fromJson(json, GameConfig.class);
            }
            catch(IOException e) {
                throw new GameEngineException(e);
            }
        }
        else {
            log.info("No configuration file exists. Creating one...");
            config = new GameConfig();
            config.setResources(new Resources());
            configDirty = true;
        }



        //Scan images
        if(imageScanDirectory != null) {
            if(config.getResources().getImages() == null) {
                config.getResources().setImages(new ArrayList<>());
            }
            scanImages(config);
            configDirty = true;
        }

        if(configDirty) {
            saveConfig(config);
        }

        JsonValidator.validateThenThrow(config);
        return config;
    }

    public void saveConfig(GameConfig config) {
        log.info("Saving config: " + configLocation);
        JsonValidator.validateThenThrow(config);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        try {
            FileUtils.writeStringToFile(new File(configLocation), json, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void scanImages(GameConfig config) {
        removeDeletedFiles((List<SingleFileConfig>)(List<?>)config.getResources().getImages());
        List<Path> imagesToLoad = scanResources((List<SingleFileConfig>)(List<?>)config.getResources().getImages(),
                imageScanDirectory, new String[]{"png", "tga"});

        for(Path path : imagesToLoad) {
            ImageConfig imageConfig = new ImageConfig();
            imageConfig.setFilename(getRelativePath(path, ""));
            imageConfig.setKey(getRelativePath(path, imageScanDirectory));
            config.getResources().getImages().add(imageConfig);
        }
    }

    private void removeDeletedFiles(List<SingleFileConfig> fileConfigs) {
        fileConfigs.removeIf(c -> !(new File(c.getFilename()).exists()));
    }

    private List<Path> scanResources(List<SingleFileConfig> existingResources, String directory, String[] fileExtentions) {
        try {
            return Files.walk(Paths.get(new File(directory).toURI()))
            .filter(Files::isRegularFile)
            .filter(p -> Arrays.asList(fileExtentions).contains(FilenameUtils.getExtension(p.toString().toLowerCase())))
            .filter(p -> notInConfig(existingResources, p))
            .collect(Collectors.toList());
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    private boolean notInConfig(List<SingleFileConfig> configs, Path path) {
        return configs.stream().noneMatch(c -> c.getFilename().equals(getRelativePath(path, "")));
    }

    private String getRelativePath(Path path, String directory) {
        Path basePath = Paths.get(directory);
        return basePath.toUri().relativize(new File(path.toUri()).toURI()).toString();
    }
}
