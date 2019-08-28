package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.config.json.ScriptConfig;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.utils.file.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ScriptLoader extends ResourceLoader {
    private List<ScriptConfig> scriptsToLoad;

    public ScriptLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getScriptManager().unloadGroups(ctx);
        scriptsToLoad = new ArrayList<>();

        if(game.getConfig().getResources().getScripts() != null) {
            scriptsToLoad.addAll(game.getConfig().getResources().getScripts()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        ScriptConfig config = scriptsToLoad.remove(0);
        DataSource location = config.getResourceMapper().get((config.getFilename()));
        Script script = game.getScriptEngine().loadScript(location);
        game.getScriptManager().put(config.getKey(), script);
        log.info("Script loaded: " + config.getFilename());
    }

    @Override
    public int getRemaining() {
        return scriptsToLoad.size();
    }
}
