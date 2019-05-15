package net.loganford.noideaengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.config.SingleFileConfig;
import net.loganford.noideaengine.config.json.AudioConfig;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.config.json.ImageConfig;
import net.loganford.noideaengine.config.json.Resources;
import net.loganford.noideaengine.utils.JsonValidator;
import net.loganford.noideaengine.utils.file.FileResourceLocation;
import net.loganford.noideaengine.utils.file.FileResourceLocationFactory;
import net.loganford.noideaengine.utils.file.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ConfigurationLoader {
    @Getter @Setter private boolean resourceScanningEnabled = true;
    @Getter @Setter private String imageScanDirectory = "data/images/";
    @Getter @Setter private String audioScanDirectory = "data/audio/";
    @Getter @Setter private ResourceLocation configLocation = new FileResourceLocation(new File("game.json"));

    public GameConfig loadConfiguration(Game game) {
        GameConfig config;
        boolean configDirty = false;

        if (configLocation.exists()) {
            log.info("Loading configuration file: " + configLocation);
            Gson gson = new Gson();

            String json = configLocation.load();
            config = gson.fromJson(json, GameConfig.class);
        } else {
            log.info("No configuration file exists. Creating one...");
            config = new GameConfig();
            config.setResources(new Resources());
            configDirty = true;
        }


        //Scan images
        if (configLocation.isSaveSupported()) {
            if (game.getResourceLocationFactory() instanceof FileResourceLocationFactory) {
                if (imageScanDirectory != null) {
                    if (config.getResources().getImages() == null) {
                        config.getResources().setImages(new ArrayList<>());
                    }
                    scanImages(config);
                    configDirty = true;
                }
            }

            if (configDirty) {
                saveConfig(config);
            }
        }

        //Scan audio
        if (configLocation.isSaveSupported()) {
            if (game.getResourceLocationFactory() instanceof FileResourceLocationFactory) {
                if (audioScanDirectory != null) {
                    if (config.getResources().getAudio() == null) {
                        config.getResources().setAudio(new ArrayList<>());
                    }
                    scanAudio(config);
                    configDirty = true;
                }
            }

            if (configDirty) {
                saveConfig(config);
            }
        }

        JsonValidator.validateThenThrow(config);
        return config;
    }

    public void saveConfig(GameConfig config) {
        log.info("Saving config: " + configLocation);
        JsonValidator.validateThenThrow(config);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        configLocation.save(json);
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

    @SuppressWarnings("unchecked")
    private void scanAudio(GameConfig config) {
        removeDeletedFiles((List<SingleFileConfig>)(List<?>)config.getResources().getAudio());
        List<Path> audioToLoad = scanResources((List<SingleFileConfig>)(List<?>)config.getResources().getAudio(),
                audioScanDirectory, new String[]{"ogg"});

        for(Path path : audioToLoad) {
            AudioConfig audioConfig = new AudioConfig();
            audioConfig.setFilename(getRelativePath(path, ""));
            audioConfig.setKey(getRelativePath(path, audioScanDirectory));
            config.getResources().getAudio().add(audioConfig);
        }
    }

    private void removeDeletedFiles(List<SingleFileConfig> fileConfigs) {
        fileConfigs.removeIf(c -> !(new File(c.getFilename()).exists()));
    }

    private List<Path> scanResources(List<SingleFileConfig> existingResources, String directory, String[] fileExtentions) {
        File directoryFile = new File(directory);
        if(directoryFile.exists()) {
            try {
                return Files.walk(Paths.get(directoryFile.toURI()))
                        .filter(Files::isRegularFile)
                        .filter(p -> Arrays.asList(fileExtentions).contains(FilenameUtils.getExtension(p.toString().toLowerCase())))
                        .filter(p -> notInConfig(existingResources, p))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new GameEngineException(e);
            }
        }
        else {
            return new ArrayList<>();
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
