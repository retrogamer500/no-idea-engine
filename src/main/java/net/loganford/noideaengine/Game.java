package net.loganford.noideaengine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.audio.Audio;
import net.loganford.noideaengine.audio.AudioSystem;
import net.loganford.noideaengine.config.ConfigurationLoader;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.graphics.*;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.resources.ResourceManager;
import net.loganford.noideaengine.resources.loading.*;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.scripting.ScriptEngine;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.scripting.ScriptedEntity;
import net.loganford.noideaengine.scripting.engine.javascript.JsScriptEngine;
import net.loganford.noideaengine.state.GameState;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.state.loading.BasicLoadingScreen;
import net.loganford.noideaengine.state.loading.LoadingScreen;
import net.loganford.noideaengine.state.transition.InstantTransition;
import net.loganford.noideaengine.state.transition.Transition;
import net.loganford.noideaengine.utils.file.DataSource;
import net.loganford.noideaengine.utils.file.FileDataSource;
import net.loganford.noideaengine.utils.file.FileResourceMapper;
import net.loganford.noideaengine.utils.file.ResourceMapper;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import net.loganford.noideaengine.utils.performance.FramerateMonitor;
import net.loganford.noideaengine.utils.performance.PerformanceTracker;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unchecked")
@Log4j2
public class Game {

    public static final long NANOSECONDS_IN_SECOND = 1000000000;
    public static final long NANOSECONDS_IN_MILLISECOND = 1000000;
    public static final long MILLISECONDS_IN_SECOND = 1000;

    public static final long SLEEP_BUFFER_MS = 2;

    @Getter private Window window;
    @Getter(onMethod = @__({@Scriptable})) private Input input;
    @Getter private Renderer renderer;

    private boolean running = true;

    @Getter private GameState gameState;
    private GameState nextGameState;
    /**The transition which will be used between states*/
    @Getter @Setter private Transition transition = new InstantTransition();
    /**The loading screen which will be used between states and at the beginning of the game*/
    @Getter @Setter private LoadingScreen loadingScreen = new BasicLoadingScreen();

    @Getter private int maxFps;
    @Getter private int minFps;
    private long lastFrameTime;
    private long maxFrameTimeNs = NANOSECONDS_IN_SECOND / 144L;
    private long minFrameTimeNs = NANOSECONDS_IN_SECOND / 60L;

    @Getter private ConfigurationLoader configurationLoader;
    /**Alarm system used by the game*/
    @Getter private AlarmSystem alarms;
    /**Audio system which may be used to play sounds and music*/
    @Getter private AudioSystem audioSystem;

    /**List of persistent entities between scenes*/
    @Getter private List<Entity> persistentEntities = new ArrayList<>();

    /**Default resource mapper to use when converting resource paths in the config.json into files*/
    @Getter @Setter private ResourceMapper resourceMapper = new FileResourceMapper(new File(""));
    @Getter @Setter private DataSource configSource = new FileDataSource(new File("game.json"));

    /**Keep track of loaded resource groups*/
    @Getter private HashSet<Integer> loadedResourceGroups = new HashSet<>();

    @Getter @Setter private ScriptEngine scriptEngine = new JsScriptEngine();

    //Resource managers
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Image> imageManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Texture> textureManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<CubeMap> cubeMapManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<ShaderProgram> shaderManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Model> modelManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Sprite> spriteManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Font> fontManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Audio> audioManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<Script> scriptManager = new ResourceManager<>();
    @Getter(onMethod = @__({@Scriptable})) private ResourceManager<ScriptedEntity> entityManager = new ResourceManager<>();

    //Measure fps and performance of engine
    private FramerateMonitor framerateMonitor;
    private PerformanceTracker renderTimeTracker, updateTimeTracker, idleTimeTracker;

    /**
     * Creates a game. You must call {@link #run()} later to actually begin the game loop.
     * @param gameState the initial state for the game
     */
    public Game(GameState gameState) {
        input = new Input();
        window = new Window(this);
        renderer = new Renderer(this);
        alarms = new AlarmSystem();
        audioSystem = new AudioSystem();
        configurationLoader = new ConfigurationLoader();
        framerateMonitor = new FramerateMonitor();
        renderTimeTracker = framerateMonitor.getPerformanceTracker("render time");
        updateTimeTracker = framerateMonitor.getPerformanceTracker("update time");
        idleTimeTracker = framerateMonitor.getPerformanceTracker("idle time");

        setFps(60,144);
        alarms.add(10000, true, UnsafeMemoryTracker::printMemoryStatus);

        //Initialize a loading screen to load initial state
        if(gameState instanceof LoadingScreen || gameState instanceof Transition) {
            throw new GameEngineException("Cannot set initial state to be a loading screen or transition");
        }
        LoadingContext ctx = new LoadingContext(this, gameState);
        if(ctx.isLoadingRequired()) {
            this.gameState = loadingScreen;
            loadingScreen.beginLoadingScreen(ctx, gameState);
            loadedResourceGroups.addAll(ctx.getLoadingGroups());
        }
        else {
            this.gameState = gameState;
        }
    }

    /**
     * Loads the game configuration.
     */
    protected void loadConfiguration() {
        configurationLoader.load(resourceMapper, configSource);
    }

    /**
     * Initializes the OpenGL context.
     */
    protected void startGame() {
        log.info("Initializing OpenGL context");
        getWindow().init();

        log.info("Initializing renderer");
        renderer.init();

        log.info("Setting up game states");
        gameState.beginState(this);
        gameState.postBeginState(this);
    }

    /**
     * Starts the game and enters the game loop.
     */
    public void run() {
        log.info("Loading configuration");
        loadConfiguration();
        log.info("Initializing game");
        startGame();

        log.info("Entering game loop");
        lastFrameTime = System.nanoTime();
        framerateMonitor.start();
        idleTimeTracker.start();

        while(running) {
            long currentTime = System.nanoTime();
            long deltaTimeNs = currentTime - lastFrameTime;

            long sleepTimeMs = ((maxFrameTimeNs - deltaTimeNs)/ NANOSECONDS_IN_MILLISECOND) - SLEEP_BUFFER_MS;
            if(sleepTimeMs > 0) {
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }

            if (deltaTimeNs > maxFrameTimeNs) {
                if (deltaTimeNs > minFrameTimeNs) {
                    //Alert low FPS if FPS falls below 3 percent of target
                    if (deltaTimeNs > minFrameTimeNs * 1.03) {
                        log.warn("Low FPS!");
                    }
                    deltaTimeNs = minFrameTimeNs;
                }

                //Update input
                input.stepInput(window);
                lastFrameTime = currentTime;

                step((float)deltaTimeNs / NANOSECONDS_IN_MILLISECOND);

                //Handle input
                window.step();
                input.swapInputBuffers();
            }

            //Exit the game if requested by the window
            if (getWindow().closeRequested()) {
                endGame();
            }

        }

        terminate();
    }

    /**
     * Steps and renders the current state. May be overridden to apply additional game-wide functionality like exiting
     * the game when escape is pressed.
     * @param delta time since last frame in milliseconds
     */
    protected void step(float delta) {
        //Update window state
        getWindow().swapBuffers();
        getWindow().pollEvents();
        getWindow().clearFramebuffer();

        //Step
        idleTimeTracker.end();
        updateTimeTracker.start();
        alarms.step(delta);
        gameState.stepState(this, delta);
        updateTimeTracker.end();

        //Render
        renderTimeTracker.start();
        gameState.renderState(this, this.getRenderer());
        gameState.getFrameBufferObject().renderToScreen(this, gameState, renderer);
        renderTimeTracker.end();
        idleTimeTracker.start();

        Renderer.errorCheck();
        window.setTitle("FPS: " + framerateMonitor.getFramesPerSecond());

        //Performance Tracking
        framerateMonitor.update();

        //Handle state transitions
        handleTransitions();

        window.step();
    }

    /**
     * Called before the game terminates.
     */
    protected void terminate() {
        transition.endState(this);
        gameState.endState(this);
        audioSystem.freeMemory();
        GLFW.glfwTerminate();
    }

    /**
     * Handles the transitions between states.
     */
    protected void handleTransitions() {
        while(nextGameState != null) {
            /*We must handle beginning and ending the state for transitions here. We don't need to call this for regular
            * states or loading screens since the transitions that transition to/away from those states call their begin
            * and end state methods.*/
            if(gameState instanceof Transition) {
                transition.endState(this);
            }
            gameState = nextGameState;
            nextGameState = null;
            if(gameState instanceof Transition) {
                gameState.beginState(this);
                gameState.postBeginState(this);
            }
        }
    }

    /**
     * Called whenever the window is resized.
     * @param width new width of the window
     * @param height new height of the window
     */
    public void onResize(int width, int height) {
        gameState.onResize(width, height);
    }

    /**
     * Call this to end the game.
     */
    public void endGame() {
        log.info("Ending game");
        running = false;
    }

    /**
     * Sets the fps of the game.
     * @param min min fps, if the frames drops below this, the game will slow down
     * @param max target fps
     */
    public void setFps(int min, int max) {
        this.minFps = min;
        this.maxFps = max;

        maxFrameTimeNs = NANOSECONDS_IN_SECOND / (long)max;
        minFrameTimeNs = NANOSECONDS_IN_SECOND / (long)min;
    }

    /**
     * Returns a list of resource loaders which will be used to load resources from the filesystem into memory and
     * initialize the resources.
     * @return the list of loaders
     */
    public List<ResourceLoader> getResourceLoaders() {
        List<ResourceLoader> resourceLoaders = new ArrayList<>();
        resourceLoaders.add(new ShaderLoader(this));
        resourceLoaders.add(new ImageLoader(this));
        resourceLoaders.add(new TextureLoader(this));
        resourceLoaders.add(new CubeMapLoader(this));
        resourceLoaders.add(new ImageAtlasPacker(this));
        resourceLoaders.add(new ModelLoader(this));
        resourceLoaders.add(new SpriteLoader(this));
        resourceLoaders.add(new FontLoader(this));
        resourceLoaders.add(new AudioLoader(this));
        resourceLoaders.add(new ScriptLoader(this));
        resourceLoaders.add(new EntityLoader(this));
        return resourceLoaders;
    }

    /**
     * Changes the game state.
     * @param requestedNextState new state
     */
    public void setState(GameState requestedNextState) {
        //If the current state is a transition, we simply go to the next state.
        //Otherwise, if loading is required we go to a loading screen, but if not we go to a transition.
        if (nextGameState == null) {
            if (!(gameState instanceof Transition)) {
                LoadingContext ctx = new LoadingContext(this, requestedNextState);
                if (ctx.isLoadingRequired()) {
                    loadingScreen.beginLoadingScreen(ctx, requestedNextState);
                    requestedNextState = loadingScreen;
                    loadedResourceGroups.removeAll(ctx.getUnloadingGroups());
                    loadedResourceGroups.addAll(ctx.getLoadingGroups());
                }

                transition.beginTransition(this, gameState, requestedNextState);
                nextGameState = transition;
            } else {
                nextGameState = requestedNextState;
            }
        }
    }

    /**
     * Shortcut to getConfigurationLoader().getConfig().
     * @return the current game configuration
     */
    public GameConfig getConfig() {
        return getConfigurationLoader().getConfig();
    }
}
