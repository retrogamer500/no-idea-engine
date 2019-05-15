package net.loganford.noideaengine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.jodah.typetools.TypeResolver;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.audio.Audio;
import net.loganford.noideaengine.audio.AudioSystem;
import net.loganford.noideaengine.config.json.GameConfig;
import net.loganford.noideaengine.graphics.*;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.resources.ResourceManager;
import net.loganford.noideaengine.resources.loading.*;
import net.loganford.noideaengine.state.GameState;
import net.loganford.noideaengine.state.entity.AbstractEntity;
import net.loganford.noideaengine.state.loading.BasicLoadingScreen;
import net.loganford.noideaengine.state.loading.LoadingScreen;
import net.loganford.noideaengine.state.transition.InstantTransition;
import net.loganford.noideaengine.state.transition.Transition;
import net.loganford.noideaengine.utils.*;
import net.loganford.noideaengine.utils.file.FileResourceLocationFactory;
import net.loganford.noideaengine.utils.file.ResourceLocationFactory;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*Warnings are suppressed as we type-check the GameState class when the method setState is called*/
@SuppressWarnings("unchecked")
@Log4j2
public class Game {


    public static final long NANOSECONDS_IN_SECOND = 1000000000;
    public static final long NANOSECONDS_IN_MILLISECOND = 1000000;
    public static final long MILLISECONDS_IN_SECOND = 1000;

    public static final long SLEEP_BUFFER_MS = 2;

    @Getter private Window window;
    @Getter private Input input;
    @Getter private Renderer renderer;
    private FramerateMonitor framerateMonitor;
    private PerformanceTracker renderTimeTracker, updateTimeTracker, idleTimeTracker;
    @Getter private ConfigurationLoader configurationLoader;
    @Getter private GameConfig config;
    @Getter private AlarmSystem alarms;
    @Getter private AudioSystem audioSystem;

    @Getter @Setter private ResourceLocationFactory resourceLocationFactory = new FileResourceLocationFactory(new File(""));
    //Keep track of loaded resource groups
    @Getter private HashSet<Integer> loadedResourceGroups = new HashSet<>();
    //Resource managers
    @Getter private ResourceManager<Image> imageManager = new ResourceManager<>();
    @Getter private ResourceManager<Texture> textureManager = new ResourceManager<>();
    @Getter private ResourceManager<ShaderProgram> shaderManager = new ResourceManager<>();
    @Getter private ResourceManager<Model> modelManager = new ResourceManager<>();
    @Getter private ResourceManager<Sprite> spriteManager = new ResourceManager<>();
    @Getter private ResourceManager<Font> fontManager = new ResourceManager<>();
    @Getter private ResourceManager<Audio> audioManager = new ResourceManager<Audio>();

    private boolean running = true;
    @Getter private GameState gameState;
    private GameState nextGameState;
    @Getter @Setter private Transition transition = new InstantTransition();
    @Getter private LoadingScreen loadingScreen = new BasicLoadingScreen();

    @Getter private int maxFps;
    @Getter private int minFps;
    private long lastFrameTime;
    private long maxFrameTimeNs = NANOSECONDS_IN_SECOND / 144L;
    private long minFrameTimeNs = NANOSECONDS_IN_SECOND / 60L;

    //Used to store persistent entities between states
    @Getter @Setter private List<AbstractEntity> persistentEntities = new ArrayList<>();

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
        }
        else {
            this.gameState = gameState;
        }
    }

    /**
     * Loads configuration file, and initializes the OpenGL context
     */
    private void startGame() {
        config = configurationLoader.loadConfiguration(this);

        log.info("Initializing OpenGL context");
        getWindow().init();

        log.info("Initializing renderer");
        renderer.init();

        log.info("Setting up game states");
        gameState.beginState(this);
        gameState.postBeginState(this);
    }

    public void run() {
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

                //Update window state
                getWindow().swapBuffers();
                getWindow().pollEvents();
                getWindow().clearFramebuffer();

                //Step
                float deltaTime = (float)deltaTimeNs / NANOSECONDS_IN_MILLISECOND;
                idleTimeTracker.end();
                updateTimeTracker.start();
                alarms.step(deltaTime);
                gameState.stepState(this, deltaTime);
                updateTimeTracker.end();

                //Render
                renderTimeTracker.start();
                gameState.renderState(this, this.getRenderer());
                gameState.getFrameBufferObject().renderToScreen(this, gameState, renderer);
                renderTimeTracker.end();
                idleTimeTracker.start();

                checkForErrors();
                window.setTitle("FPS: " + framerateMonitor.getFramesPerSecond());

                //Performance Tracking
                framerateMonitor.update();

                if(input.keyReleased(GLFW.GLFW_KEY_ESCAPE)) {
                    endGame();
                }

                //Handle state transitions
                handleTransitions();

                window.step();

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

    private void terminate() {
        transition.endState(this);
        gameState.endState(this);
        audioSystem.freeMemory();
        GLFW.glfwTerminate();
    }

    protected void handleTransitions() {
        if(nextGameState != null) {
            if (!(gameState instanceof Transition)) {
                transition.beginTransition(this, gameState, nextGameState);
                transition.beginState(this);
                transition.postBeginState(this);
                gameState = transition;
            }
        }
        if(nextGameState != null) {
            //noinspection ConstantConditions (left for clarity)
            if (gameState instanceof Transition) {
                transition.endState(this);
                transition.getPreviousState().endState(this);
                gameState = transition.getNextState();
                nextGameState = null;
            }
        }
    }

    public void onResize(int width, int height) {
        gameState.onResize(width, height);
    }

    public void endGame() {
        log.info("Ending game");
        running = false;
    }

    public void setFps(int min, int max) {
        this.minFps = min;
        this.maxFps = max;

        maxFrameTimeNs = NANOSECONDS_IN_SECOND / (long)max;
        minFrameTimeNs = NANOSECONDS_IN_SECOND / (long)min;
    }

    private void checkForErrors() {
        Renderer.errorCheck();
    }

    public List<ResourceLoader> getResourceLoaders() {
        List<ResourceLoader> resourceLoaders = new ArrayList<>();
        resourceLoaders.add(new ShaderLoader(this));
        resourceLoaders.add(new ImageLoader(this));
        resourceLoaders.add(new TextureLoader(this));
        resourceLoaders.add(new ImageAtlasPacker(this));
        resourceLoaders.add(new ModelLoader(this));
        resourceLoaders.add(new SpriteLoader(this));
        resourceLoaders.add(new FontLoader(this));
        resourceLoaders.add(new AudioLoader(this));
        return resourceLoaders;
    }

    public void setState(GameState state) {
        if(state != null) {
            if (state instanceof Transition || state instanceof LoadingScreen) {
                throw new GameEngineException("Cannot directly set state to transitions or loading screens.");
            }

            Class<?> generic = TypeResolver.resolveRawArgument(GameState.class, state.getClass());
            if (!getClass().isAssignableFrom(generic)) {
                throw new GameEngineException("Invalid GameState: state cannot be casted due to generics.");
            }
        }

        this.nextGameState = state;
    }
}
