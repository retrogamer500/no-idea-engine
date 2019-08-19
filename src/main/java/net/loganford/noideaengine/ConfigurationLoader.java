package net.loganford.noideaengine;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.config.LoadableConfig;
import net.loganford.noideaengine.config.SingleFileConfig;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.utils.file.AbstractResource;
import net.loganford.noideaengine.utils.file.AbstractResourceMapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ConfigurationLoader {
    private Gson gson;

    public ConfigurationLoader() {
        gson = new Gson();
    }

    public GameConfig loadConfiguration(Game game, AbstractResourceMapper abstractResourceMapper, AbstractResource configLocation) {
        GameConfig config;

        if (configLocation.exists()) {
            log.info("Loading configuration file: " + configLocation);
            String json = configLocation.load();
            config = gson.fromJson(json, GameConfig.class);
            processResources(config, abstractResourceMapper);
        } else {
            throw new GameEngineException("No configuration file exists.");
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    public void loadAdditionalResources(Game game, AbstractResourceMapper abstractResourceMapper, AbstractResource newConfigLocation, GameConfig existingConfig) {
        if (newConfigLocation.exists()) {
            log.info("Loading configuration file: " + newConfigLocation);
            String json = newConfigLocation.load();
            GameConfig newConfig = gson.fromJson(json, GameConfig.class);
            processResources(newConfig, abstractResourceMapper);

            //Copy new resources into existing config
            try {
                for (Field listField : newConfig.getResources().getClass().getDeclaredFields()) {
                    listField.setAccessible(true);
                    if (List.class.isAssignableFrom(listField.getType())) {
                        List list = (List)listField.get(newConfig.getResources());
                        if(list != null && list.size() > 0) {
                            Object first = list.get(0);
                            if(first instanceof LoadableConfig) {
                                List existingList = (List)listField.get(existingConfig.getResources());
                                if(existingList == null) {
                                    existingList = new ArrayList();
                                }
                                existingList.addAll(list);
                            }
                        }
                    }
                }
            }
            catch(IllegalAccessException e) {
                throw new GameEngineException(e);
            }

        } else {
            throw new GameEngineException("No configuration file exists.");
        }
    }

    @SuppressWarnings("unchecked")
    private void processResources(GameConfig config, AbstractResourceMapper abstractResourceMapper) {
        try {
            for (Field listField : config.getResources().getClass().getDeclaredFields()) {
                listField.setAccessible(true);
                if (List.class.isAssignableFrom(listField.getType())) {
                    List list = (List)listField.get(config.getResources());
                    if(list != null && list.size() > 0) {
                        Object first = list.get(0);
                        if(first instanceof LoadableConfig) {
                            for(LoadableConfig loadableConfig : (List<LoadableConfig>) list) {
                                loadableConfig.setAbstractResourceMapper(abstractResourceMapper);
                                expandGlob(loadableConfig, abstractResourceMapper);
                            }
                        }
                    }
                }
            }
        }
        catch(IllegalAccessException e) {
            throw new GameEngineException(e);
        }
    }

    private void expandGlob(LoadableConfig loadableConfig, AbstractResourceMapper abstractResourceMapper) {
        if(loadableConfig instanceof SingleFileConfig) {
            SingleFileConfig singleFileConfig = (SingleFileConfig) loadableConfig;
            abstractResourceMapper.expandGlob(singleFileConfig.getFilename(), (glob) -> {});
        }
    }
}
