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
import net.loganford.noideaengine.state.entity.*;
import net.loganford.noideaengine.state.entity.EntitySystemEngine;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.util.*;

@Log4j2
public class Scene<G extends Game> extends GameState<G> {
    private G game;

    @Getter private boolean sceneBegun = false;
    //List of entities in the scene
    @Getter private EntityStore entities;
    private int currentEntity = 0;
    @Getter private EntitySystemEngine entitySystemEngine;

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
                entity.postCreate(this);
            }
        }
        else {
            throw new GameEngineException("Tried to add entity to scene with improper generics: " + entity.getClass().getName());
        }
    }

    private boolean testEntityGenerics(Entity e) {
        Class<?>[] generics = TypeResolver.resolveRawArguments(Entity.class, e.getClass());
        return generics[0].isAssignableFrom(game.getClass()) && generics[1].isAssignableFrom(getClass());
    }


    @Override
    public void beginState(G game) {
        super.beginState(game);
        this.game = game;
        entitySystemEngine = new EntitySystemEngine(game, this);
        entities = new EntityStore();
        collisionSystem2D.init();
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

        Iterator<Entity> it = game.getPersistentEntities().iterator();
        while(it.hasNext()) {
            Entity entity = it.next();
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
            Entity entity = entities.get(currentEntity);
            entity.onCreate(game, this);
            entity.postCreate(this);
        }
    }

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

        collisionSystem2D.destroy();
        entities = null;
    }

    public <C extends Entity> void with(Class<C> clazz, EntityAction<C> action) {
        for(Entity entity : entities.byClass(clazz)) {
            C castedEntity = clazz.cast(entity);
            action.doAction(castedEntity);
        }
    }

    public <C extends Entity> C nearest(Class<C> clazz, float x, float y) {
        C returnValue = null;
        float minDisSqr = Float.MAX_VALUE;

        for(Entity entity : entities.byClass(clazz)) {
            //AssignableFrom method above avoids any exceptions
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, casted.getX(), casted.getY());
            if(disSqr < minDisSqr) {
                minDisSqr = disSqr;
            }
        }

        return returnValue;
    }

    public <C extends Entity> C furthest(Class<C> clazz, float x, float y) {
        C returnValue = null;
        float maxDisSqr = Float.MAX_VALUE;

        for(Entity entity : entities.byClass(clazz)) {
            @SuppressWarnings("unchecked") C casted = (C) entity;
            float disSqr = MathUtils.distanceSqr(x, y, casted.getX(), casted.getY());
            if(disSqr > maxDisSqr) {
                maxDisSqr = disSqr;
                returnValue = casted;
            }
        }

        return returnValue;
    }

    public <C extends Entity> List<EntityDistancePair<C>> nearest(Class<C> clazz, float x, float y, int count) {
        List<EntityDistancePair<C>> pairs = new ArrayList<>(count);

        for(Entity entity : entities.byClass(clazz)) {
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
        return pairs;
    }

    public <C extends Entity> int count(Class<C> clazz) {
        return entities.byClass(clazz).size();
    }
}
