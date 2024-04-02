package net.loganford.noideaengine.resources.loading;

import net.loganford.nieEditorImporter.ProjectImporter;
import net.loganford.noideaengine.Game;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

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
            importer.load(new File(game.getConfig().getResources().getProject()));
            game.setEditorProject(importer.getProject());
        }

        done = true;
    }

    @Override
    public int getRemaining() {
        return done ? 0 : 1;
    }
}
