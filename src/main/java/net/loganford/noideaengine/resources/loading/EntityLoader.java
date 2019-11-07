package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.EntityConfig;
import net.loganford.noideaengine.scripting.Function;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.scripting.ScriptedEntity;
import net.loganford.noideaengine.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EntityLoader extends ResourceLoader {
    private List<EntityConfig> entitiesToLoad;

    public EntityLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        entitiesToLoad = new ArrayList<>();

        if(game.getConfig().getResources().getEntities() != null) {
            entitiesToLoad.addAll(game.getConfig().getResources().getEntities()
                    .stream().filter(r -> !game.getEntityManager().exists(r.getKey())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        EntityConfig config = entitiesToLoad.remove(0);
        ScriptedEntity entity = load(game, config);
        populateResource(entity, config);
        game.getEntityManager().put(config.getKey(), entity);
        log.info("Loaded entity: " + config.getKey());
    }

    @Override
    public int getRemaining() {
        return entitiesToLoad.size();
    }

    @SuppressWarnings("unchecked")
    public ScriptedEntity load(Game game, EntityConfig config) {
        Script script = game.getScriptManager().get(config.getScriptKey());
        Function createFunction = script.getFunction(config.getFunction());
        Class entityClass = createFunction.evalObject(Class.class);
        if(entityClass != null && Entity.class.isAssignableFrom(entityClass)) {
            return new ScriptedEntity(entityClass);
        }
        else {
            throw new GameEngineException("Script unable to load entity");
        }

    }
}
