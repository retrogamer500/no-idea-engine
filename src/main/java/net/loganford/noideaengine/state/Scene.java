package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.jodah.typetools.TypeResolver;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import net.loganford.noideaengine.state.collisionSystem.CollisionSystem2D;
import net.loganford.noideaengine.state.collisionSystem.NaiveBroadphase;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.entity.AbstractEntity;
import net.loganford.noideaengine.state.entity.Entity2D;
import net.loganford.noideaengine.state.entity.EntityAction;
import net.loganford.noideaengine.state.entity.EntityDistancePair;
import net.loganford.noideaengine.utils.MathUtils;

import java.util.*;

@Log4j2
public class Scene extends GameState {
    private Game game;

    private boolean sceneBegun = false;
    private ArrayList<AbstractEntity> depthChangedEntities;
    @Getter private HashMap<Class<? extends AbstractEntity>, List<AbstractEntity>> entitiesByClass;
    /** List of entities in the scene. May contain destroyed entities if they are destroyed earlier in the frame. */
    @Getter private ArrayList<AbstractEntity> entities;
    private int currentEntity = 0;

    @Getter @Setter private CollisionSystem2D collisionSystem2D = new NaiveBroadphase();

    /**
     * Adds an entity to this scene.
     * @param entity the entity to add
     */
    /*
    We suppress a lot of unchecked warnings in this whole class. The generics are tested here within the
    testEntityGenerics method as the generics are way to complicated to check at compile-time. A GameEngineException
    will be thrown if an Entity with incorrect generics is added. This could easily be changed to not add the entity,
    but the developer would probably prefer to know if they make this mistake.
     */
    @SuppressWarnings("unchecked")
    public void add(AbstractEntity entity) {
        log.debug("Adding entity: " + entity.getClass().getName() + " Entity count: " + entities.size());
        if(testEntityGenerics(entity)) {
            entity.setScene(this);
            entity.setGame(game);
            entity.setDepthChanged(false);

            if (!entitiesByClass.containsKey(entity.getClass())) {
                entitiesByClass.put(entity.getClass(), new ArrayList<>());
            }
            entitiesByClass.get(entity.getClass()).add(entity);

            int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
            if(index < 0) {
                index = -(index + 1);
            }
            entities.add(index, entity);

            if(index <= currentEntity) {
                currentEntity++;
            }

            if(sceneBegun) {
                entity.onCreate(game, this);
                entity.postCreate(this);
            }
        }
        else {
            throw new GameEngineException("Tried to add entity to scene with improper generics: " + entity.getClass().getName());
        }
    }

    private boolean testEntityGenerics(AbstractEntity e) {
        Class<?>[] generics = TypeResolver.resolveRawArguments(Entity2D.class, e.getClass());
        return generics[0].isAssignableFrom(game.getClass()) && generics[1].isAssignableFrom(getClass());
    }


    @Override
    public void beginState(Game game) {
        super.beginState(game);
        this.game = game;
        depthChangedEntities = new ArrayList<>();
        entitiesByClass = new HashMap<>();
        entities = new ArrayList<>();
        collisionSystem2D.init();
    }

    /**
     * This method is called after the beginState method. If overridden, make sure you call super. This method is useful
     * to add events that are triggered after entities are added to the room and their create method is called.
     * @param game the current game
     */
    @SuppressWarnings("unchecked")
    @Override
    public void postBeginState(Game game) {
        super.postBeginState(game);

        Iterator<AbstractEntity> it = game.getPersistentEntities().iterator();
        while(it.hasNext()) {
            AbstractEntity entity = it.next();
            if(testEntityGenerics(entity)) {
                if(testEntityGenerics(entity)) {
                    add(entity);
                    it.remove();
                }
                else {
                    log.info("Tried to add persistent entity to scene with improper generics: " + entity.getClass().getName());
                }
            }
        }

        sceneBegun = true;

        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            AbstractEntity entity = entities.get(currentEntity);
            entity.onCreate(game, this);
            entity.postCreate(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void step(Game game, long delta) {
        super.step(game, delta);

        //Resort entities which have had their depth changed
        for(int i = entities.size() - 1; i >= 0; i--) {
            AbstractEntity entity = entities.get(i);
            if(entity.isDepthChanged()) {
                entities.remove(i);
                entity.setDepthChanged(false);
                depthChangedEntities.add(entity);
            }
        }

        for(AbstractEntity entity : depthChangedEntities) {
            int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
            if(index < 0) {
                index = -(index + 1);
            }
            entities.add(index, entity);
        }
        depthChangedEntities.clear();

        //Step entities
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            AbstractEntity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.beforeStep(game, this, delta);
            }
        }
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            AbstractEntity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.step(game, this, delta);
            }
        }
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            AbstractEntity entity = entities.get(currentEntity);
            if(!entity.isDestroyed()) {
                entity.afterStep(game, this, delta);
            }
        }

        //Delete destroyed entities
        for(int i = entities.size() - 1; i >= 0; i--) {
            if(entities.get(i).isDestroyed()) {
                log.debug("Removing entity: " + entities.get(i).getClass().getName() + " Entity count: " + entities.size());
                entities.remove(i);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(Game game, Renderer renderer) {
        for(AbstractEntity entity : entities) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void endState(Game game) {
        super.endState(game);
        sceneBegun = false;

        for(AbstractEntity entity : entities) {
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

        collisionSystem2D.destroy();
        depthChangedEntities = null;
        entitiesByClass = null;
        entities = null;
    }

    public <C extends AbstractEntity> void with(Class<C> clazz, EntityAction<C> action) {
        for (Map.Entry<Class<? extends AbstractEntity>, List<AbstractEntity>> map : entitiesByClass.entrySet()) {
            if (clazz.isAssignableFrom(map.getKey())) {
                for (AbstractEntity entity : map.getValue()) {
                    C castedEntity = clazz.cast(entity);
                    action.doAction(castedEntity);
                }
            }
        }
    }

    public <C extends Entity2D> C nearest(Class<C> clazz, float x, float y) {
        C returnValue = null;
        float minDisSqr = Float.MAX_VALUE;

        for (Map.Entry<Class<? extends AbstractEntity>, List<AbstractEntity>> map : entitiesByClass.entrySet()) {
            if (clazz.isAssignableFrom(map.getKey())) {
                for (AbstractEntity entity : map.getValue()) {
                    //AssignableFrom method above avoids any exceptions
                    @SuppressWarnings("unchecked") C casted = (C) entity;
                    float disSqr = MathUtils.distanceSqr(x, y, casted.getX(), casted.getY());
                    if(disSqr < minDisSqr) {
                        minDisSqr = disSqr;
                        returnValue = casted;
                    }
                }
            }
        }

        return returnValue;
    }

    public <C extends Entity2D> C furthest(Class<C> clazz, float x, float y) {
        C returnValue = null;
        float maxDisSqr = Float.MAX_VALUE;

        for (Map.Entry<Class<? extends AbstractEntity>, List<AbstractEntity>> map : entitiesByClass.entrySet()) {
            if (clazz.isAssignableFrom(map.getKey())) {
                for (AbstractEntity entity : map.getValue()) {
                    //AssignableFrom method above avoids any exceptions
                    @SuppressWarnings("unchecked") C casted = (C) entity;
                    float disSqr = MathUtils.distanceSqr(x, y, casted.getX(), casted.getY());
                    if(disSqr > maxDisSqr) {
                        maxDisSqr = disSqr;
                        returnValue = casted;
                    }
                }
            }
        }

        return returnValue;
    }

    public <C extends Entity2D> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, int count) {
        List<EntityDistancePair<C>> pairs = new ArrayList<>(count);

        for (Map.Entry<Class<? extends AbstractEntity>, List<AbstractEntity>> map : entitiesByClass.entrySet()) {
            if (clazz.isAssignableFrom(map.getKey())) {
                for (AbstractEntity entity : map.getValue()) {
                    //AssignableFrom method above avoids any exceptions
                    @SuppressWarnings("unchecked") C casted = (C) entity;
                    float disSqr = MathUtils.distanceSqr(x, y, casted.getX(), casted.getY());
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
            }
        }
        return pairs;
    }

    public <C extends AbstractEntity> int count(Class<C> clazz) {
        int count = 0;
        for (Map.Entry<Class<? extends AbstractEntity>, List<AbstractEntity>> map : entitiesByClass.entrySet()) {
            if (clazz.isAssignableFrom(map.getKey())) {
                count += map.getValue().size();
            }
        }
        return count;
    }
}
