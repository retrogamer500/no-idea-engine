package net.loganford.noideaengine.config;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.config.json.LoadableConfig;
import net.loganford.noideaengine.config.json.SingleFileConfig;
import net.loganford.noideaengine.utils.file.DataSource;
import net.loganford.noideaengine.utils.file.ResourceMapper;
import net.loganford.noideaengine.utils.json.JsonValidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ConfigurationLoader {
    @Getter private GameConfig config;
    @Getter private Gson gson;

    public ConfigurationLoader() {
        gson = new Gson();
    }

    /**
     * Loads a game configuration. If the configuration has already been loaded, then add new resources to the existing
     * configuration.
     * @param resourceMapper resource mapper for all the resources loaded
     * @param configSource the data source of the config file
     */
    public void load(ResourceMapper resourceMapper, DataSource configSource) {
        if(config == null) {
            load(resourceMapper, configSource, true);
        }
        else {
            load(resourceMapper, configSource, false);
        }
    }

    public void load(ResourceMapper resourceMapper, DataSource configSource, boolean overwriteConfig) {
        try {
            if(!configSource.exists()) {
                throw new GameEngineException("No configuration file exists.");
            }

            log.info("Loading configuration file: " + configSource);
            String json = configSource.load();
            GameConfig loadedConfig = gson.fromJson(json, GameConfig.class);
            JsonValidator.validateThenThrow(loadedConfig);
            populateResourceMappers(loadedConfig, resourceMapper);
            expandGlobs(loadedConfig, resourceMapper);

            if(overwriteConfig) {
                this.config = loadedConfig;
            }
            else {
                interleaveGameConfigurations(this.config, loadedConfig);
            }
        }
        catch(Exception e) {
            throw new GameEngineException(e);
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
    private void expandGlobs(GameConfig base, ResourceMapper resourceMapper) throws IllegalAccessException {
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

    private List<SingleFileConfig> expandGlob(SingleFileConfig singleFileConfig, ResourceMapper resourceMapper) {
        List<SingleFileConfig> resultList = new ArrayList<>();

        resourceMapper.expandGlob(singleFileConfig.getFilename().substring(5), (resourceKey, captureGroups) -> {
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

    private void populateResourceMappers(GameConfig gameConfig, ResourceMapper resourceMapper) {
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
