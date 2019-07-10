package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.FrameBufferObject;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import net.loganford.noideaengine.resources.RequireGroup;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class GameState<G extends Game> implements UnsafeMemory {

    @Getter @Setter private Camera camera;
    @Getter @Setter private View view;
    private List<UILayer> uiLayers;
    @Getter private FrameBufferObject frameBufferObject;
    private Vector4f clearColor;
    private Matrix4f M4 = new Matrix4f();

    @Getter @Setter private float scale = 1; /*Todo: implement*/
    @Getter @Setter private boolean stretch = false;

    @Getter @Setter private float width;
    @Getter @Setter private float height;

    @Getter private AlarmSystem alarms;
    private G game;

    /**
     * Called to initialize the state when the game switches to it. Override to add custom logic, but be sure to
     * call super.
     * @param game
     */
    public void beginState(G game) {
        this.game = game;
        view = new View(game, this, (int)(game.getWindow().getWidth() / scale), (int)(game.getWindow().getHeight() / scale));
        width = view.getWidth();
        height = view.getHeight();
        camera = new Camera(game, this);
        frameBufferObject = new FrameBufferObject(game, (int)(game.getWindow().getWidth() / scale), (int)(game.getWindow().getHeight() / scale), 1, true);
        clearColor = new Vector4f(0f, 0f, 0f, 1f);
        alarms = new AlarmSystem();
        uiLayers = new ArrayList<>();
    }

    public void postBeginState(G game) {}

    public final void stepState(G game, float delta) {
        int stepDepth = getStepDepth();
        int inputDepth = getInputDepth();

        if(inputDepth != -1) {
            game.getInput().disable();
        }

        if(stepDepth == -1) {
            step(game, delta);
        }

        for(int i = Math.max(0, stepDepth); i < uiLayers.size(); i++) {
            if(i == inputDepth) {
                game.getInput().enable();
            }
            uiLayers.get(i).step(game, this, delta);
        }

        //remove destroyed UILayers
        for(int i = uiLayers.size() - 1; i >= 0; i--) {
            if(uiLayers.get(i).isDestroyed()) {
                uiLayers.remove(i);
            }
        }

        game.getInput().enable();
    }

    /**
     * Called once per game loop. Most of your game logic will take place within this method, including moving entities.
     * The delta time is passed in in order to allow frame-rate independence. Override to add custom logic, but be
     * sure to call super.
     * @param game
     * @param delta
     */
    public void step(G game, float delta) {
        view.step();
        camera.step();
        alarms.step(delta);
    }

    /**
     * Final method. Sets the current target for drawing to the state's FBO, then calls the render method.
     * @param game
     * @param renderer
     */
    public final void renderState(G game, Renderer renderer) {
        //Calculate viewMatrix
        view.beforeRender(this);
        camera.beforeRender(this);

        if(stretch) {
            GL33.glViewport(0, 0, (int)(640 / scale), (int)(480 / scale));
        }
        else {
            GL33.glViewport(0, 0, view.getWidth(), view.getHeight());
        }

        //Use FBO
        frameBufferObject.use();
        GL33.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

        //Render scene
        renderer.setView(getView());
        renderer.setCamera(getCamera());
        int renderDepth = getRenderDepth();
        if(renderDepth == -1) {
            render(game, renderer);
        }
        renderer.getTextureBatch().flush(renderer);

        //Render UI
        Matrix4f oldView = getView().getViewMatrix();
        renderer.getView().setViewMatrix(M4.identity());
        renderUI(game, renderer);
        renderer.getTextureBatch().flush(renderer);
        renderer.getView().setViewMatrix(oldView);

        //Render FBO
        GL33.glViewport(0, 0, game.getWindow().getWidth(), game.getWindow().getHeight());
        FrameBufferObject.useDefault();
    }

    /**
     * The render method. This gets called once per loop. You may do all the rendering here. As with all of these methods,
     * if you override, be sure to call super().
     * @param game
     * @param renderer
     */
    public void render(G game, Renderer renderer) {

    }

    public void renderUI(G game, Renderer renderer) {
        int renderDepth = getRenderDepth();
        for(int i = Math.max(0, renderDepth); i < uiLayers.size(); i++) {
            uiLayers.get(i).render(game, this, renderer);
        }
    }

    /**
     * Called when the state is ended. Override to add custom logic.
     * @param game
     */
    public void endState(G game) {
        freeMemory();
    }

    @Override
    public final void freeMemory() {
        frameBufferObject.freeMemory();
    }

    /**
     * Set the color of the background.
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public void setBackgroundColor(float r, float g, float b, float a) {
        clearColor.set(r, g, b, a);
    }

    /**
     * Called whenever the window is resized. This method performs logic to resize the FBO if necessary.
     * @param width
     * @param height
     */
    public void onResize(int width, int height) {
        if(view != null) {
            if(!stretch) {
                view.onResize((int)(width / scale), (int)(height / scale));
                GL33.glViewport(0, 0, view.getWidth(), view.getHeight());
            }
        }

        if(frameBufferObject != null) {
            if(!stretch) {
                frameBufferObject.freeMemory();
                frameBufferObject = new FrameBufferObject(game, (int)(width / scale), (int)(height / scale), 1, true);
            }
        }
    }

    /**
     * Returns a List of resource groups required by this level. If this contains a resource group which is not currently
     * loaded by the game, then prior to loading this state, a loading screen will appear and the required resources
     * will be loaded.
     * @return
     */
    public List<Integer> getRequiredResourceGroups() {
        List<Integer> requiredResources = new ArrayList<>();
        requiredResources.add(0);

        for (Annotation annotation : getClass().getDeclaredAnnotations()) {
            if(annotation instanceof RequireGroup.List) {
                RequireGroup.List requireGroupList = (RequireGroup.List) annotation;
                for(RequireGroup requireGroup: requireGroupList.value()) {
                    requiredResources.add(requireGroup.value());
                }
            }
        }

        return requiredResources;
    }

    public void restart() {
        game.setState(this);
    }

    public void addUILayer(UILayer layer) {
        uiLayers.add(layer);
        //Todo: check if UI layer is valid for this state
        layer.beginUILayer(game, this);
    }

    /**
     * @return the first UI layer to render, or -1 if this state should be rendered
     */
    public final int getRenderDepth() {
        for(int index = uiLayers.size() - 1; index >= 0; index--) {
            UILayer layer = uiLayers.get(index);
            if(!layer.renderBelow()) {
                return index;
            }
        }

        return -1;
    }

    /**
     * @return the first UI layer to update, or -1 if this state should be rendered
     */
    public final int getStepDepth() {
        for(int index = uiLayers.size() - 1; index >= 0; index--) {
            UILayer layer = uiLayers.get(index);
            if(!layer.stepBelow()) {
                return index;
            }
        }

        return -1;
    }

    /**
     * @return the first UI layer to send input to, or -1 if this state should be rendered
     */
    public final int getInputDepth() {
        for(int index = uiLayers.size() - 1; index >= 0; index--) {
            UILayer layer = uiLayers.get(index);
            if(!layer.inputBelow()) {
                return index;
            }
        }

        return -1;
    }
}
