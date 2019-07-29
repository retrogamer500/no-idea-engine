package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;

public abstract class AbstractEntity<G extends Game, S extends Scene<G>> {
    @Getter private boolean destroyed = false;
    @Getter private float depth = 0;
    @Getter @Setter private S scene;
    @Getter @Setter private G game;
    @Getter @Setter boolean depthChanged = false;
    @Getter @Setter private boolean persistent;

    @Getter private AlarmSystem alarms;

    /**
     * Call this method to destroy this entity. Entities are removed from the scene at the end of the game loop.
     */
    public final void destroy() {
        destroyed = true;
        onDestroy(game, scene);
        postDestroy(scene);
    }

    /**
     * Sets the depth of the entity. Entities are drawn from the entity with the highest depth to the lowest. The entity
     * with the highest depth will appear below other entities. The step method will also be called in this order.
     * @param depth
     */
    public void setDepth(float depth) {
        if(depth != this.depth) {
            depthChanged = true;
        }
        this.depth = depth;
    }

    //Event methods

    /**
     * This method is called at the beginning of the step, after the entity has been placed in the scene.
     * @param game
     * @param scene
     */
    public void onCreate(G game, S scene) {
        alarms = new AlarmSystem();
    }

    /**
     * This method is called prior to step
     * @param game
     * @param scene
     * @param delta
     */
    public void beforeStep(G game, S scene, float delta) {}

    /**
     * This method is called every step of the game loop. Delta time is passed through here. Do not call any draw
     * methods within this or any of the other step methods.
     * @param game
     * @param scene
     * @param delta
     */
    public void step(G game, S scene, float delta) {
        alarms.step(delta);
    }

    /**
     * This method is called after step
     * @param game
     * @param scene
     * @param delta
     */
    public void afterStep(G game, S scene, float delta) {}

    /**
     * This method is called once per step. Render the entity here. Do not change the state of the entity-- do that in
     * the step method.
     * @param game
     * @param scene
     * @param renderer
     */
    public void render(G game, S scene, Renderer renderer) {}

    /**
     * This method is called when the entity is destroyed. You may place custom logic here.
     * @param game
     * @param scene
     */
    public void onDestroy(G game, S scene) {}

    //Broadphase management methods
    public void postCreate(S scene) {}
    public void postDestroy(S scene) {}
    public void beforeMove(S scene) {}
    public void afterMove(S scene) {}

    public void beginScene(G game, S scene) {}
    public void endScene(G game, S scene) {}
}
