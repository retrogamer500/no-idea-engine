package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.jodah.typetools.TypeResolver;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.state.entity.*;
import net.loganford.noideaengine.state.entity.systems.EntitySystem;
import net.loganford.noideaengine.state.entity.systems.InheritSystems;
import net.loganford.noideaengine.state.entity.systems.RegisterSystem;
import net.loganford.noideaengine.state.entity.systems.UnregisterSystem;
import net.loganford.noideaengine.state.entity.systems.collision.CollisionSystem;
import net.loganford.noideaengine.state.entity.systems.collision.SpacialPartitionCollisionSystem;
import net.loganford.noideaengine.utils.math.MathUtils;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
@RegisterSystem(SpacialPartitionCollisionSystem.class)
public class Scene<G extends Game> extends GameState<G> {
    private G game;
    private int currentEntity = 0;

    @Getter private boolean sceneBegun = false;
    //List of entities in the scene
    @Getter(onMethod = @__({@Scriptable})) private SimpleEntityStore entities;
    //ECS Engine
    @Getter private EntitySystemEngine entitySystemEngine;
    //Cache of collision system. Move to Entity in the future?
    @Getter(onMethod = @__({@Scriptable})) CollisionSystem collisionSystem;

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     */
    @Scriptable
    @SuppressWarnings("unchecked")
    public void add(Entity entity) {
        log.debug("Adding entity: " + entity.getClass().getName() + " Entity count: " + entities.size());
        if(testEntityGenerics(entity)) {
            entity.setScene(this);
            entity.setGame(game);
            entity.setDepthChanged(false);

            int index = entities.add(entity);
            if(index <= currentEntity) {
                currentEntity++;
            }

            if(sceneBegun) {
                entity.onCreate(game, this);
            }
        }
        else {
            throw new GameEngineException("Tried to add entity to scene with improper generics: " + entity.getClass().getName());
        }
    }

    /**
     * Called to initialize the state. May be overridden to do things such as populate the scene with entities. Be sure
     * to call super otherwise the scene will error out.
     * @param game the game
     */
    @Override
    public void beginState(G game) {
        super.beginState(game);
        this.game = game;
        entities = new SimpleEntityStore();
        entitySystemEngine = new EntitySystemEngine(game, this);
        loadSystems();
    }

    /**
     * This method is called after the beginState method. If overridden, make sure you call super. This method is useful
     * to add events that are triggered after entities are added to the room and their create method is called.
     * @param game the current game
     */
    @SuppressWarnings("unchecked")
    @Override
    public void postBeginState(G game) {
        super.postBeginState(game);

        //Create any new entities added prior to the state beginning
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity);
            entity.onCreate(game, this);
            entity.beginScene(game, this);
        }

        //Officially mark the scene as started
        sceneBegun = true;

        //Handle persistent entities
        Iterator<Entity> it = game.getPersistentEntities().iterator();
        while(it.hasNext()) {
            Entity entity = it.next();
            if(testEntityGenerics(entity)) {
                if(testEntityGenerics(entity)) {
                    entity.getSystems().clear(); //Clear cache of systems from last scene
                    add(entity);
                    entity.beginScene(game, this);
                    it.remove();
                }
                else {
                    log.info("Tried to add persistent entity to scene with improper generics: " + entity.getClass().getName());
                }
            }
        }
    }

    /**
     * Steps the scene. May be overridden to add custom logic.
     * @param game the game
     * @param delta time since last frame, in milliseconds
     */
    @SuppressWarnings("unchecked")
    @Override
    public void step(G game, float delta) {
        super.step(game, delta);

        //Resort entities which have had their depth changed
        entities.resort();

        //Step entities
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.beforeStep(game, this, delta);
            }
        }

        entitySystemEngine.step(delta);

        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.step(game, this, delta);
            }
        }
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.afterStep(game, this, delta);
            }
        }

        //Delete destroyed entities
        entities.removeDestroyed();
    }

    /**
     * Renders the scene.
     * @param game the game
     * @param renderer the renderer
     */
    @SuppressWarnings("unchecked")
    @Override
    public void render(Game game, Renderer renderer) {
        entitySystemEngine.render(renderer);
        for(Entity entity : entities) {
            entity.render(game, this, renderer);
        }
    }

    /**
     * Adds a tile layer to this particular room
     * @param tileImage image which contains the tiles
     * @param width width of the tile layer, in tiles
     * @param height height of the tile layer, in tiles
     * @param tileWidth width of each tile, in pixels
     * @param tileHeight height of each tile, in pixels
     * @param depth depth of tile layer
     * @return the tile layer added to the room
     */
    public TileLayer addTileLayer(Image tileImage, int width, int height, float tileWidth, float tileHeight, float depth) {
        TileLayer layer = new TileLayer(tileImage, width, height, tileWidth, tileHeight);
        layer.setDepth(depth);
        entities.add(layer);
        return layer;
    }

    /**
     * Called by the engine when the scene is ended. You may override, but be sure to call super. You may wish to call
     * super as the last line of the overridden method, otherwise all of the entities will have been destroyed.
     * @param game the game
     */
    @SuppressWarnings("unchecked")
    @Override
    public void endState(G game) {
        super.endState(game);
        sceneBegun = false;

        for(Entity entity : entities) {
            entity.endScene(game, this);

            if(entity.isPersistent()) {
                game.getPersistentEntities().add(entity);
            }
            else {
                if(entity instanceof UnsafeMemory) {
                    ((UnsafeMemory)entity).freeMemory();
                }
            }
        }

        entities = null;
    }

    /**
     * Performs an action for each entity of a certain class
     * @param clazz the class to search for
     * @param action the action to perform
     */
    public <C extends Entity> void with(Class<C> clazz, EntityAction<C> action) {
        for(Entity entity : entities.byClass(clazz)) {
            C castedEntity = clazz.cast(entity);
            action.doAction(castedEntity);
        }
    }

    /**
     * Gets the nearest entity to a location.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @return the nearest entity to a location
     */
    @Scriptable
    public <C extends Entity> C nearest(Class<C> clazz, float x, float y) {
        return nearest(clazz, x, y, 0f);
    }

    /**
     * Gets the nearest entity to a location.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @param z position in world space
     * @return the nearest entity to a location
     */
    @Scriptable
    public <C extends Entity> C nearest(Class<C> clazz, float x, float y, float z) {
        C returnValue = null;
        float minDisSqr = Float.MAX_VALUE;

        for(Entity entity : entities.byClass(clazz)) {
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, casted.getX(), casted.getY(), casted.getZ());
            if(disSqr < minDisSqr) {
                minDisSqr = disSqr;
                returnValue = casted;
            }
        }

        return returnValue;
    }

    /**
     * Gets the furthest entity to a location.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @return the furthest entity to a location
     */
    @Scriptable
    public <C extends Entity> C furthest(Class<C> clazz, float x, float y) {
        return furthest(clazz, x, y, 0);
    }

    /**
     * Gets the furthest entity to a location.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @param z position in world space
     * @return the furthest entity to a location
     */
    @Scriptable
    public <C extends Entity> C furthest(Class<C> clazz, float x, float y, float z) {
        C returnValue = null;
        float maxDisSqr = Float.MAX_VALUE;

        for(Entity entity : entities.byClass(clazz)) {
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, casted.getX(), casted.getY(), casted.getZ());
            if(disSqr > maxDisSqr) {
                maxDisSqr = disSqr;
                returnValue = casted;
            }
        }

        return returnValue;
    }

    /**
     * Gets the N closest entities to a location, sorted nearest to furthest.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @param count the number of entities to find
     * @return a list of nearby entities
     */
    @Scriptable
    public <C extends Entity> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, int count) {
        return nearest(clazz, x, y, 0, count);
    }


    /**
     * Gets the N closest entities to a location, sorted nearest to furthest.
     * @param clazz class of entity to find
     * @param x position in world space
     * @param y position in world space
     * @param z position in world space
     * @param count the number of entities to find
     * @return a list of nearby entities
     */
    @Scriptable
    public <C extends Entity> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, float z, int count) {
        List<EntityDistancePair<C>> pairs = new ArrayList<>(count);

        for(Entity entity : entities.byClass(clazz)) {
            //AssignableFrom method above avoids any exceptions
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, casted.getX(), casted.getY(), casted.getZ());
            EntityDistancePair<C> pair = new EntityDistancePair<>(casted, disSqr);

            if(pairs.size() == 0) {
                pairs.add(pair);
            }
            else {
                for(int i = 0; i < pairs.size(); i++) {
                    EntityDistancePair<C> potentialPair = pairs.get(i);
                    if(potentialPair.getDistanceSqr() > pair.getDistanceSqr()) {
                        pairs.add(i, pair);
                        break;
                    }
                }
            }

            if(pairs.size() > count) {
                pairs.remove(count);
            }
        }
        return pairs;
    }

    /**
     * Counts the total number of entities of a certain class.
     * @param clazz Entity or a subclass
     * @return the number of entities in the scene that are, or are a subclass of, the parameter
     */
    @Scriptable
    public <C extends Entity> int count(Class<C> clazz) {
        return entities.byClass(clazz).size();
    }

    /**
     * Tests whether an entity can be placed in the scene.
     * @param e the entity to test
     * @return whether the entity's generics allow it to be entered into this scene.
     */
    private boolean testEntityGenerics(Entity e) {
        Class<?>[] generics = TypeResolver.resolveRawArguments(Entity.class, e.getClass());
        return generics[0].isAssignableFrom(game.getClass()) &&
                (generics[1] == TypeResolver.Unknown.class || generics[1].isAssignableFrom(getClass()));
    }

    /**
     * Loads and creates all the systems in the scene based off of the annotations.
     */
    private void loadSystems() {
        Class clazz = getClass();
        List<Class<? extends EntitySystem>> systemClazzList = getSystemsForClass(getClass());

        for(Class<? extends EntitySystem> systemClazz : systemClazzList) {
            try {
                Constructor<? extends EntitySystem> constructor = systemClazz.getConstructor();
                EntitySystem system = constructor.newInstance();

                if(system instanceof CollisionSystem) {
                    collisionSystem = (CollisionSystem)system;
                }

                entitySystemEngine.addSystem(system);
            }
            catch(Exception e) {
                throw new GameEngineException("Unable to setup entity components", e);
            }
        }
    }

    private List<Class<? extends EntitySystem>> getSystemsForClass(Class clazz) {
        List<Class<? extends EntitySystem>> systemClazzList = new ArrayList<>();
        if(clazz != null) {

            Annotation inherit = clazz.getAnnotation(InheritSystems.class);
            if(inherit == null || ((InheritSystems) inherit).value()) {
                systemClazzList.addAll(getSystemsForClass(clazz.getSuperclass()));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(RegisterSystem.class)) {
                Class<? extends EntitySystem> systemClass = ((RegisterSystem)annotation).value();
                systemClazzList.add(systemClass);
            }

            for(Annotation annotation : clazz.getAnnotationsByType(UnregisterSystem.class)) {
                Class<? extends EntitySystem> systemClass = ((UnregisterSystem)annotation).value();
                systemClazzList.remove(systemClass);
            }
        }
        return systemClazzList;
    }
}
