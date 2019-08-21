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
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ConfigurationLoader {
    private Gson gson;

    public ConfigurationLoader() {
        gson = new Gson();
    }

    public GameConfig loadConfiguration(Game game, AbstractResourceMapper resourceMapper, AbstractResource configLocation) {
        GameConfig config;

        if (configLocation.exists()) {
            try {
                log.info("Loading configuration file: " + configLocation);
                String json = configLocation.load();
                config = gson.fromJson(json, GameConfig.class);
                populateResourceMappers(config, resourceMapper);
                expandGlobs(config, resourceMapper);
            }
            catch(Exception e) {
                throw new GameEngineException(e);
            }
        } else {
            throw new GameEngineException("No configuration file exists.");
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    public void loadAdditionalResources(Game game, AbstractResourceMapper resourceMapper, AbstractResource newConfigLocation, GameConfig existingConfig) {
        if (newConfigLocation.exists()) {
            log.info("Loading configuration file: " + newConfigLocation);
            String json = newConfigLocation.load();
            GameConfig newConfig = gson.fromJson(json, GameConfig.class);

            //Copy new resources into existing config
            try {
                populateResourceMappers(newConfig, resourceMapper);
                expandGlobs(newConfig, resourceMapper);
                interleaveGameConfigurations(existingConfig, newConfig);
            }
            catch(Exception e) {
                throw new GameEngineException(e);
            }

        } else {
            throw new GameEngineException("No configuration file exists.");
        }
    }

    @SuppressWarnings("unchecked")
    private void interleaveGameConfigurations(GameConfig base, GameConfig extra) throws IllegalAccessException {
        List<Field> fields = getResourceListsOfType(extra, LoadableConfig.class);
        for(Field field : fields) {
            List<LoadableConfig> baseList = (List<LoadableConfig>)field.get(base.getResources());
            List<LoadableConfig> extraList = (List<LoadableConfig>)field.get(extra.getResources());

            if(baseList == null) {
                baseList = new ArrayList<>();
                field.set(base.getResources(), baseList);
            }

            baseList.addAll(extraList);
        }
    }

    @SuppressWarnings("unchecked")
    private void expandGlobs(GameConfig base, AbstractResourceMapper resourceMapper) throws IllegalAccessException {
        List<Field> fields = getResourceListsOfType(base, SingleFileConfig.class);
        for(Field field : fields) {
            List<SingleFileConfig> configList = (List<SingleFileConfig>)field.get(base.getResources());
            List<SingleFileConfig> newConfigs = new ArrayList<>();
            Iterator<SingleFileConfig> iterator = configList.iterator();
            while(iterator.hasNext()) {
                SingleFileConfig config = iterator.next();
                if (config.getFilename().startsWith("glob:")) {
                    newConfigs.addAll(expandGlob(config, resourceMapper));
                    iterator.remove();
                }
            }
            configList.addAll(newConfigs);
        }
    }

    private List<SingleFileConfig> expandGlob(SingleFileConfig singleFileConfig, AbstractResourceMapper abstractResourceMapper) {
        List<SingleFileConfig> resultList = new ArrayList<>();

        abstractResourceMapper.expandGlob(singleFileConfig.getFilename().substring(5), (resourceKey, captureGroups) -> {
            try {
                SingleFileConfig newConfig = singleFileConfig.clone();
                newConfig.setFilename(resourceKey);
                resultList.add(newConfig);

                //Iterate through the fields of the single file config
                Class clazz = newConfig.getClass();
                while(LoadableConfig.class.isAssignableFrom(clazz)) {
                    for (Field stringField : clazz.getDeclaredFields()) {
                        stringField.setAccessible(true);
                        //And update group references in string fields
                        if (String.class.isAssignableFrom(stringField.getType()) && !stringField.getName().equals("filename")) {
                            String string = (String) stringField.get(newConfig);
                            for (int i = 0; i < captureGroups.size(); i++) {
                                string = string.replaceAll("\\{" + i + "}", captureGroups.get(i));
                            }
                            stringField.set(newConfig, string);
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }
            catch(CloneNotSupportedException | IllegalAccessException e) {
                throw new GameEngineException("Unable to process game configuration.", e);
            }
        });

        return resultList;
    }

    @SuppressWarnings("unchecked")
    private List<Field> getResourceListsOfType(GameConfig gameConfig, Class clazz) throws IllegalAccessException {
        List<Field> resultList = new ArrayList<>();
        for (Field listField : gameConfig.getResources().getClass().getDeclaredFields()) {
            listField.setAccessible(true);
            if (List.class.isAssignableFrom(listField.getType())) {
                List list = (List) listField.get(gameConfig.getResources());
                if (list != null && list.size() > 0) {
                    Object first = list.get(0);
                    if (clazz.isAssignableFrom(first.getClass())) {
                        resultList.add(listField);
                    }
                }
            }
        }
        return resultList;
    }

    private void populateResourceMappers(GameConfig gameConfig, AbstractResourceMapper resourceMapper) {
        for(LoadableConfig config : getConfigsWithClass(gameConfig, LoadableConfig.class)) {
            config.setResourceMapper(resourceMapper);
        }
    }

    @SuppressWarnings("unchecked")
    private <C extends LoadableConfig> List<C> getConfigsWithClass(GameConfig gameConfig, Class<C> clazz) {
        List<C> returnList = new ArrayList<>();

        try {
            for (Field listField : gameConfig.getResources().getClass().getDeclaredFields()) {
                listField.setAccessible(true);
                if (List.class.isAssignableFrom(listField.getType())) {
                    List list = (List) listField.get(gameConfig.getResources());
                    if (list != null && list.size() > 0) {
                        Object first = list.get(0);
                        if (clazz.isAssignableFrom(first.getClass())) {
                            for(Object object : list) {
                                returnList.add((C)object);
                            }
                        }
                    }
                }
            }
        }
        catch(IllegalAccessException e) {
            throw new GameEngineException("Unable to process game configuration.", e);
        }

        return returnList;
    }
}
