package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.FrameBufferObject;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.resources.RequireGroup;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class GameState implements UnsafeMemory {
    /**The state's camera object, which controls how 3D objects are rendered.*/
    @Getter @Setter private Camera camera;
    /**The state's view object, controls how 2D objects are rendered.*/
    @Getter @Setter private View view;
    private List<UILayer> uiLayers;
    /**The state's alarms.*/
    @Getter private AlarmSystem alarms;

    /**The scale of the framebuffer. Higher values are more zoomed in and pixelated. Keep the value an integer for best results.*/
    @Getter @Setter private float scale = 1;
    /**Whether the view should stretch, or show more of the state when the window is resized.*/
    @Getter @Setter private boolean stretch = false;

    /**The width of the state. Currently does not affect too much.*/
    @Getter @Setter private float width;
    /**The height of the state. Currently does not affect too much.*/
    @Getter @Setter private float height;

    /**The framebuffer used for rendering anything in this state.*/
    @Getter private FrameBufferObject frameBufferObject;
    /**The background color of the state.*/
    @Getter @Setter private Vector4f backgroundColor;

    private Game game;

    /**
     * Called to initialize the state when the game switches to it. Override to add custom logic, but be sure to
     * call super.
     * @param game the game
     */
    public void beginState(Game game) {
        this.game = game;
        view = new View(game, this, (int)(game.getWindow().getWidth() / scale), (int)(game.getWindow().getHeight() / scale));
        width = view.getWidth();
        height = view.getHeight();
        camera = new Camera(game, this);
        backgroundColor = new Vector4f(0f, 0f, 0f, 1f);
        alarms = new AlarmSystem();
        uiLayers = new ArrayList<>();
    }

    /**
     * Called after beginState has been called.
     * @param game the game
     */
    public void postBeginState(Game game) {
        if(isStretch()) {
            frameBufferObject = new FrameBufferObject(game, view.getWidth(), view.getHeight(), 1, true);
        }
        else {
            frameBufferObject = new FrameBufferObject(game, (int) (game.getWindow().getWidth() / scale), (int) (game.getWindow().getHeight() / scale), 1, true);
        }
    }

    /**
     * Steps the state. This method handles stepping any UI layers and typically should only be called by the engine.
     * @param game the game
     * @param delta time since last frame, in milliseconds
     */
    public final void stepState(Game game, float delta) {
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
     * Called once per frame. Most of your game logic will take place within this method.
     * The delta time is passed in in order to allow frame-rate independence. Override to add custom logic, but be
     * sure to call super.
     * @param game the game
     * @param delta time since last frame, in milliseconds
     */
    public void step(Game game, float delta) {
        view.step();
        camera.step();
        alarms.step(delta);
    }

    /**
     * Final method. Sets the current target for drawing to the state's FBO, then calls the render method. This should
     * only be called by the engine, any other rendering should happen in the render method.
     * @param game the game
     * @param renderer the renderer
     */
    public final void renderState(Game game, Renderer renderer) {
        //Calculate viewMatrix
        view.beforeRender(this);
        camera.beforeRender(this);

        if(stretch) {
            GL33.glViewport(0, 0, (int)(640 / scale), (int)(480 / scale));
        }
        else {
            GL33.glViewport(0, 0, view.getWidth(), view.getHeight());
        }

        //Clear screen background to black
        GL33.glClearColor(0f, 0f, 0f, 1f);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

        //Use FBO
        frameBufferObject.use();
        GL33.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

        //Render scene
        renderer.setView(getView());
        renderer.setCamera(getCamera());
        int renderDepth = getRenderDepth();
        if(renderDepth == -1) {
            render(game, renderer);
        }
        renderer.getRenderBatcher().flush(renderer);

        //Render UI
        getView().getViewMatrix().pushMatrix().identity();
        renderUI(game, renderer);
        renderer.getRenderBatcher().flush(renderer);
        getView().getViewMatrix().popMatrix();

        //Render FBO
        if(isStretch()) {
            float ratio = Math.min( ((float) game.getWindow().getWidth() / getView().getWidth()), ((float) game.getWindow().getHeight()) / getView().getHeight());
            int offsetX = (int)((game.getWindow().getWidth() - (getView().getWidth() * ratio)) / 2f);
            int offsetY = (int)((game.getWindow().getHeight() - (getView().getHeight() * ratio)) / 2f);
            GL33.glViewport(offsetX, offsetY, (int) (getView().getWidth() * ratio), (int) (getView().getHeight() * ratio));
        }
        else {
            GL33.glViewport(0, 0, game.getWindow().getWidth(), game.getWindow().getHeight());
        }
        FrameBufferObject.useDefault();
    }

    /**
     * The render method. This gets called once per loop. You may do all the rendering here. As with all of these methods,
     * if you override, be sure to call super().
     * @param game the game
     * @param renderer the renderer
     */
    public void render(Game game, Renderer renderer) {

    }

    /**
     * Renders the UI stack. May be overridden. Anything rendered within this method will use screen coordinates
     * instead of world coordinates.
     * @param game the game
     * @param renderer the renderer
     */
    public void renderUI(Game game, Renderer renderer) {
        int renderDepth = getRenderDepth();
        for(int i = Math.max(0, renderDepth); i < uiLayers.size(); i++) {
            uiLayers.get(i).render(game, this, renderer);
        }
    }

    /**
     * Called before the next scene's begin state
     * @param game
     */
    public void prepareForTransition(Game game) {

    }

    /**
     * Called when the state is ended. Override to add custom logic. Failure to call super will result in framebuffer
     * memory being leaked!
     * @param game the game
     */
    public void endState(Game game) {
        freeMemory();
    }

    /**
     * Deletes the associated framebuffer.
     */
    @Override
    public final void freeMemory() {
        frameBufferObject.freeMemory();
    }

    /**
     * Set the color of the background.
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundColor.set(r, g, b, a);
    }

    /**
     * Called whenever the window is resized. This method performs logic to resize the FBO if necessary.
     * @param width desired width
     * @param height desired height
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
     * @return a list of resources which the state requires
     */
    public List<Integer> getRequiredResourceGroups() {
        List<Integer> requiredResources = new ArrayList<>();
        requiredResources.add(0);

        for (Annotation annotation : getClass().getAnnotations()) {
            if(annotation instanceof RequireGroup.List) {
                RequireGroup.List requireGroupList = (RequireGroup.List) annotation;
                for(RequireGroup requireGroup: requireGroupList.value()) {
                    requiredResources.add(requireGroup.value());
                }
            }
        }

        return requiredResources;
    }

    /**
     * Calling this will restart the state
     */
    public void restart() {
        game.setState(this);
    }

    /**
     * Adds a UI Layer to the UI stack.
     * @param layer
     */
    public void addUILayer(UILayer layer) {
        uiLayers.add(layer);
        layer.beginUILayer(game, this);
    }

    /**
     * Determines how deep through the UI stack we should render.
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
     * Determines how deep through the UI stack we should step.
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
     * Determines how deep through the UI stack we should send input.
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
