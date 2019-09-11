package net.loganford.noideaengine.config;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.config.json.LoadableConfig;
import net.loganford.noideaengine.utils.file.DataSource;
import net.loganford.noideaengine.utils.file.ResourceMapper;
import net.loganford.noideaengine.utils.glob.Glob;
import net.loganford.noideaengine.utils.glob.GlobActionInterface;
import net.loganford.noideaengine.utils.json.JsonValidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class ConfigurationLoader {

    @Getter @Setter private Class<? extends GameConfig> configurationClass = GameConfig.class;
    @Getter private GameConfig config;
    private Gson gson;


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
            GameConfig loadedConfig = gson.fromJson(json, configurationClass);
            JsonValidator.validateThenThrow(loadedConfig);
            populateResourceMappers(loadedConfig, resourceMapper);
            scanAndExpandGlobs(loadedConfig, resourceMapper);

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

    /**
     * Iterates through the currently loaded configuration file, and tries to find configs with globs in them. Then,
     * attempts to expand the glob.
     * @param base
     * @param resourceMapper
     */
    @SuppressWarnings("unchecked")
    private void scanAndExpandGlobs(GameConfig base, ResourceMapper resourceMapper) throws IllegalAccessException {
        //Iterate through resources and find lists
        List<Field> resourceFields = getAllFields(base.getResources().getClass());
        for(Field resourceField : resourceFields) {
            resourceField.setAccessible(true);
            Object fieldValue = resourceField.get(base.getResources());
            if(fieldValue instanceof List) {
                //List found
                List list = (List) fieldValue;
                if(list.size() > 0 && list.get(0) instanceof LoadableConfig) {
                    //Iterate through configs in list
                    for(int i = list.size() - 1; i >= 0; i--) {
                        Object object = list.get(i);
                        if(object instanceof LoadableConfig) {
                            LoadableConfig loadableConfig = (LoadableConfig) object;
                            //See if config contains a glob. If it does, expand it and remove current config.
                            List<LoadableConfig> expansion = tryToExpandConfig(base, loadableConfig);
                            if(expansion != null) {
                                list.addAll(expansion);
                                list.remove(i);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Searches a config for any glob fields. Fields are globable if they have the @GlobField annotation, and they
     * are a string which begins with "glob:". If such a field exists, then returns a list of new configs generated from
     * that glob. Otherwise, if no globable field exists, returns null.
     * @param base
     * @param config
     * @return
     */
    private List<LoadableConfig> tryToExpandConfig(GameConfig base, LoadableConfig config) throws IllegalAccessException {
        String glob = null;
        GlobField annotation = null;
        List<Field> fields = getAllFields(config.getClass());
        Field globField = null;
        for(Field field: fields) {
            field.setAccessible(true);
            annotation = field.getAnnotation(GlobField.class);
            if(annotation != null) {
                Object object = field.get(config);
                globField = field;
                if(object instanceof String) {
                    if(((String)object).startsWith("glob:")) {
                        glob = (String) object;
                    }
                }
                break;
            }
        }


        Field finalGlobField = globField;
        List<LoadableConfig> resultList = new ArrayList<>();

        if(glob != null) {
            //Glob is not null
            GlobActionInterface globActionInterface = (resourceKey, captureGroups) -> {
                try {
                    LoadableConfig newConfig = config.clone();
                    finalGlobField.set(newConfig, resourceKey);
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
            };

            if(annotation.value() == GlobType.FILE) {
                config.getResourceMapper().expandGlob(glob.substring(5), globActionInterface);
            }
            else if(annotation.value() == GlobType.IMAGE_KEY) {
                expandKeyGlob(glob.substring(5), globActionInterface, base.getResources().getImages());
            }
            else if(annotation.value() == GlobType.SCRIPT_KEY) {
                expandKeyGlob(glob.substring(5), globActionInterface, base.getResources().getScripts());
            }

            return resultList;
        }

        return null;
    }

    private void expandKeyGlob(String glob, GlobActionInterface globAction, List<? extends LoadableConfig> configsToMatch) {
        Pattern pattern = Glob.globToRegex(glob);
        for(LoadableConfig config : configsToMatch) {
            Matcher matcher = pattern.matcher(config.getKey());

            if(matcher.matches()) {
                List<String> groups = new ArrayList<>();
                for(int i = 0; i <= matcher.groupCount(); i++) {
                    groups.add(matcher.group(i));
                }

                globAction.doAction(config.getKey(), groups);
            }
        }
    }

    private List<Field> getAllFields(Class clazz) {
        List<Field> returnList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        if(!Object.class.equals(clazz.getSuperclass())) {
            returnList.addAll(getAllFields(clazz.getSuperclass()));
        }

        return returnList;
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
