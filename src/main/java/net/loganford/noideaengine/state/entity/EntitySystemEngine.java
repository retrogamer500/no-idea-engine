package net.loganford.noideaengine.state.entity;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.systems.AbstractEntitySystem;

import java.util.ArrayList;
import java.util.List;

public class EntitySystemEngine {
    private List<AbstractEntitySystem> systems;
    private Game game;
    private Scene scene;

    public EntitySystemEngine(Game game, Scene scene) {
        this.game = game;
        this.scene = scene;

        systems = new ArrayList<>();
    }

    /**
     * Adds a system to the engine
     * @param system
     */
    public void addSystem(AbstractEntitySystem system) {
        systems.add(system);
    }

    /**
     * Adds an entity to all the systems interested in it's components
     * @param entity the entity to add
     */
    protected void addEntityToEngine(Entity entity) {
        for(AbstractEntitySystem system : systems) {
            if(system.entityBelongs(entity)) {
                if(!entity.getSystems().contains(system)) {
                    system.addEntity(entity);
                    //Todo: what's going on with this warning?
                    entity.getSystems().add(system);
                }
            }
        }
    }

    public void step(float delta) {
        for(AbstractEntitySystem system : systems) {
            system.step(game, scene, delta);
        }
    }

    public void render(Renderer renderer) {
        for(AbstractEntitySystem system : systems) {
            system.render(game, scene, renderer);
        }
    }
}
