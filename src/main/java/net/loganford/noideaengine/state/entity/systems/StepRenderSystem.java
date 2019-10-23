package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

public class StepRenderSystem extends EntitySystem {

    private int currentEntity = 0;
    private Scene scene;

    public StepRenderSystem(Game game, Scene scene, String[] args) {
        super(game, scene, args);
        scene.getEntityAddedIndexSignal().subscribe(new EntityAddedIndexListener());
        this.scene = scene;
    }

    @Override
    public void step(Game game, Scene scene, float delta) {
        //Step entities
        for(currentEntity = 0; currentEntity < scene.getEntities().size(); currentEntity++) {
            Entity entity = scene.getEntities().get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.beforeStep(game, scene, delta);
            }
        }

        for(currentEntity = 0; currentEntity < scene.getEntities().size(); currentEntity++) {
            Entity entity = scene.getEntities().get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.step(game, scene, delta);
            }
        }
        for(currentEntity = 0; currentEntity < scene.getEntities().size(); currentEntity++) {
            Entity entity = scene.getEntities().get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.afterStep(game, scene, delta);
            }
        }
    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        for(currentEntity = 0; currentEntity < scene.getEntities().size(); currentEntity++) {
            Entity entity = scene.getEntities().get(currentEntity);
            entity.render(game, scene, renderer);
        }
    }


    private class EntityAddedIndexListener implements Listener<Integer> {
        @Override
        public void receive(Signal<Integer> signal, Integer index) {
            if(index <= currentEntity) {
                currentEntity++;
            }
        }
    }
}
