package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.entity.*;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.systems.*;
import net.loganford.noideaengine.state.signals.EntityAddedIndexSignal;
import net.loganford.noideaengine.state.signals.EntityAddedSignal;
import net.loganford.noideaengine.systems.collision.CollisionSystem;
import net.loganford.noideaengine.systems.collision.SpacialPartitionCollisionSystem;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.InheritSystems;
import net.loganford.noideaengine.utils.annotations.RegisterSystem;
import net.loganford.noideaengine.utils.annotations.UnregisterSystem;
import net.loganford.noideaengine.utils.math.MathUtils;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3fc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
@RegisterSystem(SpacialPartitionCollisionSystem.class)
@RegisterSystem(StepSystem.class)
@RegisterSystem(RenderSystem.class)
@RegisterSystem(LightingSystem.class)
public class Scene extends GameState {
    private Game game;
    private DefaultUILayer defaultUILayer;
    private int currentEntity = 0;

    @Getter private boolean sceneBegun = false;
    //List of entities in the scene
    @Getter(onMethod = @__({@Scriptable})) private SimpleEntityStore entities;
    //ECS Engine
    @Getter private EntitySystemEngine entitySystemEngine;
    //Cache of collision system. Move to Entity in the future?
    @Getter(onMethod = @__({@Scriptable})) private CollisionSystem collisionSystem;
    @Getter private LightingSystem lightingSystem;

    @Getter private EntityAddedSignal entityAddedSignal = new EntityAddedSignal();
    @Getter private EntityAddedIndexSignal entityAddedIndexSignal = new EntityAddedIndexSignal();

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     */
    @Scriptable
    public void add(Entity entity) {
        log.debug("Adding entity: " + entity.getClass().getName() + " Entity count: " + entities.size());
        entity.setScene(this);
        entity.setGame(game);
        entity.setDepthChanged(false);

        int index = entities.add(entity);

        entityAddedSignal.dispatch(entity);
        entityAddedIndexSignal.dispatch(index);

        if(index < currentEntity) {
            currentEntity++;
        }

        if(sceneBegun) {
            entity.onCreate(game, this);
        }
    }

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     * @param x the position of the entity
     * @param y the position of the entity
     */
    @Scriptable
    public void add(Entity entity, float x, float y) {
        entity.setPos(x, y);
        add(entity);
    }

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     * @param x the position of the entity
     * @param y the position of the entity
     * @param z the position of the entity
     */
    @Scriptable
    public void add(Entity entity, float x, float y, float z) {
        entity.setPos(x, y, z);
        add(entity);
    }

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     * @param position the position of the entity
     */
    @Scriptable
    public void add(Entity entity, Vector3fc position) {
        entity.setPos(position);
        add(entity);
    }

    /**
     * Called to initialize the state. May be overridden to do things such as populate the scene with entities. Be sure
     * to call super otherwise the scene will error out.
     * @param game the game
     */
    @Override
    public void beginState(Game game) {
        super.beginState(game);
        this.game = game;
        defaultUILayer = new DefaultUILayer(this);
        addUILayer(defaultUILayer);
        entities = new SimpleEntityStore();
        entitySystemEngine = new EntitySystemEngine(game, this);
        loadSystems();
    }

    /**
     * This method is called after the beginState method. If overridden, make sure you call super. This method is useful
     * to add events that are triggered after entities are added to the room and their create method is called.
     * @param game the current game
     */
    @Override
    public void postBeginState(Game game) {
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
            entity.getSystems().clear(); //Clear cache of systems from last scene
            add(entity);
            entity.beginScene(game, this);
            it.remove();
        }
    }

    /**
     * Steps the scene. May be overridden to add custom logic.
     * @param game the game
     * @param delta time since last frame, in milliseconds
     */
    @Override
    public void step(Game game, float delta) {
        super.step(game, delta);

        //Resort entities which have had their depth changed
        entities.resort();

        //Step systems
        entitySystemEngine.step(delta);

        //Delete destroyed entities
        entities.removeDestroyed(true);
    }

    /**
     * Renders the scene.
     * @param game the game
     * @param renderer the renderer
     */
    @Override
    public void render(Game game, Renderer renderer) {
        entitySystemEngine.render(renderer);
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

    @Override
    public void prepareForTransition(Game game) {
        super.prepareForTransition(game);

        for(Entity entity : entities) {
            entity.endScene(game, this);

            if(entity.isPersistent()) {
                game.getPersistentEntities().add(entity);
            }
        }
    }

    /**
     * Called by the engine when the scene is ended. You may override, but be sure to call super. You may wish to call
     * super as the last line of the overridden method, otherwise all of the entities will have been destroyed.
     * @param game the game
     */
    @Override
    public void endState(Game game) {
        super.endState(game);
        sceneBegun = false;

        for(Entity entity : entities) {
            entity.endScene(game, this);

            if(entity instanceof UnsafeMemory && !entity.isPersistent()) {
                ((UnsafeMemory)entity).freeMemory();
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
    public <C> C nearest(Class<C> clazz, float x, float y) {
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
    public <C> C nearest(Class<C> clazz, float x, float y, float z) {
        C returnValue = null;
        float minDisSqr = Float.MAX_VALUE;

        for(Object object : entities.byClass(clazz)) {
            Entity entity = (Entity) object;
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, entity.getX(), entity.getY(), entity.getZ());
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
    public <C> C furthest(Class<C> clazz, float x, float y) {
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
    public <C> C furthest(Class<C> clazz, float x, float y, float z) {
        C returnValue = null;
        float maxDisSqr = Float.MAX_VALUE;

        for(Object object : entities.byClass(clazz)) {
            Entity entity = (Entity) object;
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, entity.getX(), entity.getY(), entity.getZ());
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
    public <C> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, int count) {
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
    public <C> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, float z, int count) {
        List<EntityDistancePair<C>> pairs = new ArrayList<>(count);

        for(Object object : entities.byClass(clazz)) {
            Entity entity = (Entity) object;
            //AssignableFrom method above avoids any exceptions
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, z, entity.getX(), entity.getY(), entity.getZ());
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
     * Loads and creates all the systems in the scene based off of the annotations.
     */
    private void loadSystems() {
        log.info("Loading systems...");

        List<Pair<Class<? extends EntitySystem>, Argument[]>> systemClazzList = getSystemsForClass(getClass());

        for(Pair<Class<? extends EntitySystem>, Argument[]> systemAnnotation : systemClazzList) {
            try {
                Argument[] systemArguments = systemAnnotation.getRight();
                Class<? extends EntitySystem> systemClass = systemAnnotation.getLeft();

                Constructor<? extends EntitySystem> constructor = systemClass.getConstructor(Game.class, Scene.class, Argument[].class);
                EntitySystem system = constructor.newInstance(game, this, systemArguments);

                if(system instanceof CollisionSystem) {
                    collisionSystem = (CollisionSystem)system;
                }

                if(system instanceof LightingSystem) {
                    lightingSystem = (LightingSystem)system;
                }

                entitySystemEngine.addSystem(system);
            }
            catch(Exception e) {
                throw new GameEngineException("Unable to setup entity components", e);
            }
        }

        log.info(entitySystemEngine);
    }

    private List<Pair<Class<? extends EntitySystem>, Argument[]>> getSystemsForClass(Class clazz) {
        List<Pair<Class<? extends EntitySystem>, Argument[]>> systemClazzList = new ArrayList<>();
        if(clazz != null) {

            Annotation inherit = clazz.getAnnotation(InheritSystems.class);
            if(inherit == null || ((InheritSystems) inherit).value()) {
                systemClazzList.addAll(getSystemsForClass(clazz.getSuperclass()));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(UnregisterSystem.class)) {
                Class<? extends EntitySystem> systemClass = ((UnregisterSystem)annotation).value();
                systemClazzList.removeIf(p -> p.getLeft().equals(systemClass));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(RegisterSystem.class)) {
                Class<? extends EntitySystem> systemClass = ((RegisterSystem)annotation).value();
                Argument[] arguments = ((RegisterSystem)annotation).arguments();
                systemClazzList.add(new MutablePair<>(systemClass, arguments));
            }
        }
        return systemClazzList;
    }
}
