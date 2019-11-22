package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Property;
import net.loganford.noideaengine.config.json.PropertyConfig;
import net.loganford.noideaengine.utils.file.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class PropertyLoader extends ResourceLoader {
    private List<PropertyConfig> propertiesToLoad;

    public PropertyLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getPropertyManager().unloadGroups(ctx);
        propertiesToLoad = new ArrayList<>();

        if(game.getConfig().getResources().getProperties() != null) {
            propertiesToLoad.addAll(game.getConfig().getResources().getProperties()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        PropertyConfig config = propertiesToLoad.remove(0);
        Property property = load(config);
        populateResource(property, config);
        log.info("Property loaded: " + config.getKey() + "!");
        game.getPropertyManager().replace(config.getKey(), property);
    }

    public Property load(PropertyConfig config) {
        Property property = new Property();
        if(config.getFilename() != null) {
            DataSource location = config.getResourceMapper().get(config.getFilename());
            property.setStringValue(location.load());
        }
        else {
            property.setStringValue(config.getStringValue());
            property.setFloatValue(config.getFloatValue());
            property.setIntValue(config.getIntValue());
            property.setBooleanValue(config.isBooleanValue());
        }

        return property;
    }

    @Override
    public int getRemaining() {
        return 0;
    }
}
