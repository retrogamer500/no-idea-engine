package net.loganford.noideaengine.resources.loading;

import net.loganford.nieEditorImporter.ProjectImporter;
import net.loganford.noideaengine.Game;
import org.apache.commons.lang3.StringUtils;

public class ProjectLoader extends ResourceLoader {
    private boolean done = false;

    public ProjectLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {

    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        ProjectImporter importer = new ProjectImporter();

        if(StringUtils.isNoneEmpty(game.getConfig().getResources().getProject())) {
            importer.load(game.getResourceMapper().get(game.getConfig().getResources().getProject()).load());
            game.setEditorProject(importer.getProject());
        }

        done = true;
    }

    @Override
    public int getRemaining() {
        return done ? 0 : 1;
    }
}
