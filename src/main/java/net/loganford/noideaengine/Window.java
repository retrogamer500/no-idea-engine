package net.loganford.noideaengine;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

@Log4j2
public class Window {
    public static Boolean initialized = false;

    public static IntBuffer ib1 = BufferUtils.createIntBuffer(1);
    public static IntBuffer ib2 = BufferUtils.createIntBuffer(1);

    @Getter private int width = 640;
    @Getter private int height = 480;
    private Integer requestedWidth;
    private Integer requestedHeight;
    @Getter private boolean fullscreen = false;
    private Boolean requestedFullscreen;
    @Getter private boolean vsync = false;

    @Getter private boolean focused = true;
    @Getter private boolean mouseCaptured = false;
    @Getter private String title = "Quick Engine";

    private long window = -1;
    private Game game;


    public Window(Game game) {
        this.game = game;
    }

    public void init() {
        //Setup an error callback
        PrintWriter pw = IoBuilder.forLogger().setLevel(Level.ERROR).buildPrintWriter();
        OutputStream os = new WriterOutputStream(pw, Charset.defaultCharset());
        PrintStream ps = new PrintStream(os);
        GLFWErrorCallback.createPrint(ps).set();

        //Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure our window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        //Create the window
        window = GLFW.glfwCreateWindow(width, height, title, fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //Set up window onResize callback
        GLFW.glfwSetWindowSizeCallback(window, (window, width, height) -> {
            log.info("Window resized! Width: " + width + " Height: " + height);
            this.width = width;
            this.height = height;
            game.onResize(width, height);
        });

        //Window focus callback
        GLFW.glfwSetWindowFocusCallback(window, (window, focused) -> {
            this.focused = focused;
        });

        //Cursor position callback
        GLFW.glfwSetCursorPosCallback(window, (window, x, y) -> game.getInput().handleMouseMovement(window, x, y));

        //Setup a keyboard and mouse callbacks
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mod) -> game.getInput().handleKeyboard(window, key, scancode, action, mod));
        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mod) -> game.getInput().handleMouseButtons(window, button, action, mod));

        //Get the resolution of the primary monitor
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        //Center our window
        GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        //Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);
        //Vsynch 0/1
        GLFW.glfwSwapInterval(vsync ? 1 : 0);

        //Make the window visible
        GLFW.glfwShowWindow(window);

        //Sets up GLFW for use in current thread
        GL.createCapabilities();

        //Set the clear color
        GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if(isMouseCaptured()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }

        //Enable depth testing-- we don't want to give the player wallhacks by default =)
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_BLEND);
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
        GL33.glViewport(0, 0, width, height);
        Window.initialized = true;
    }

    public void step() {
        if(requestedWidth != null && requestedHeight != null) {
            GLFW.glfwSetWindowSize(window, requestedWidth, requestedHeight);
            requestedWidth = null;
            requestedHeight = null;
        }

        GLFW.glfwGetWindowSize(window, ib1, ib2);
        width = ib1.get(0);
        height = ib2.get(0);

        if(requestedFullscreen != null) {
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, GLFW.GLFW_DONT_CARE);
            requestedFullscreen = null;
        }
    }

    public void clearFramebuffer() {
        //Clear framebuffer
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
    }

    public void swapBuffers() {
        //Swap buffers
        GLFW.glfwSwapBuffers(window);
    }

    public void pollEvents() {
        //Poll for window events.
        GLFW.glfwPollEvents();
    }

    public boolean closeRequested() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public float getAspectRatio() {
        return ((float) width) / ((float) height);
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        if(initialized) {
            if (vsync) {
                GLFW.glfwSwapInterval(1);
            } else {
                GLFW.glfwSwapInterval(0);
            }
        }
    }

    public void setSize(int width, int height) {
        if(!initialized) {
            this.width = width;
            this.height = height;
        }
        else {
            requestedWidth = width;
            requestedHeight = height;
        }
    }

    public void setFullscreen(boolean fullscreen) {
        if(!initialized) {
            this.fullscreen = fullscreen;
        }
        else {
            requestedFullscreen = fullscreen;
        }
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
        this.title = title;
    }

    public void setMouseCaptured(boolean mouseCaptured) {
        if(window != -1) {
            if(mouseCaptured) {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            }
            else {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        }

        this.mouseCaptured = mouseCaptured;
    }
}
