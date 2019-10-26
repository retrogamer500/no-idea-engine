package net.loganford.noideaengine.state.entity;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.systems.EntitySystem;
import net.loganford.noideaengine.state.entity.systems.SystemPriorityChangedSignal;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntitySystemEngine implements Listener<EntitySystem> {
    private List<EntitySystem> systems;
    private Game game;
    private Scene scene;
    private boolean resort = false;

    public EntitySystemEngine(Game game, Scene scene) {
        this.game = game;
        this.scene = scene;

        systems = new ArrayList<>();
    }

    /**
     * Adds a system to the engine
     * @param system
     */
    public void addSystem(EntitySystem system) {
        int index = Collections.binarySearch(systems, system, (o1, o2) -> Float.compare(o1.getPriority(), o2.getPriority()));
        if(index < 0) {
            index = -(index + 1);
        }
        systems.add(index, system);
        system.getSystemPriorityChangedSignal().subscribe(this);
    }

    /**
     * Adds an entity to all the systems interested in it's components
     * @param entity the entity to add
     */
    protected void processNewEntityComponents(Entity entity) {
        for(EntitySystem system : systems) {
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
        if(resort) {
            Collections.sort(systems, (s1, s2) -> Float.compare(s1.getPriority(), s2.getPriority()));
            resort = false;
        }

        for(EntitySystem system : systems) {
            system.step(game, scene, delta);
        }
    }

    public void render(Renderer renderer) {
        for(EntitySystem system : systems) {
            system.render(game, scene, renderer);
        }
    }

    @Override
    public void receive(Signal<EntitySystem> signal, EntitySystem object) {
        if(signal instanceof SystemPriorityChangedSignal) {
            resort = true;
        }
    }
}
