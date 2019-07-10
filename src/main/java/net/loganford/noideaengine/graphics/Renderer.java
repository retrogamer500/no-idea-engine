package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.resources.loading.ModelLoader;
import net.loganford.noideaengine.resources.loading.ShaderLoader;
import net.loganford.noideaengine.resources.loading.TextureLoader;
import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.state.View;
import net.loganford.noideaengine.utils.math.MathUtils;
import net.loganford.noideaengine.utils.file.JarResourceLocationFactory;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.lang.Math;

@Log4j2
public class Renderer {
    private Game game;
    private Matrix4f matrix4f = new Matrix4f();
    @Getter private TextureBatch textureBatch;

    //Polygons for rendering shapes, private as they are stateful
    private Polygon polygonSquare;
    private Polygon polygonCenteredSquare;
    private Polygon polygonCircle;

    //Default textures
    @Getter private Texture textureWhite;
    @Getter private Texture textureBlue;
    @Getter private Texture textureBlack;
    @Getter private Texture textureCheckers;

    //Default models
    @Getter private Model modelQuad;
    @Getter private Model modelQuadFlipped;
    @Getter private Model modelCube;
    @Getter private Model model2Cube;

    //Default shaders
    @Getter private ShaderProgram shaderDefault;
    @Getter private ShaderProgram shaderSolid;
    @Getter private ShaderProgram shaderTile;

    //Render state
    @Getter private ShaderProgram shader;
    @Getter @Setter private View view;
    @Getter @Setter private Camera camera;
    private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
    @Getter private Vector3f lightDirection = new Vector3f();
    @Getter private Vector3f lightColor = new Vector3f();
    @Getter private Vector3f ambientLightColor = new Vector3f();
    @Getter private boolean cullingBackface = true;

    public Renderer(Game game) {
        this.game = game;
    }

    public void init() {
        //Initialize the bare-minimum resources necessary in order to bootstrap the game
        loadBuiltInTextures();
        loadBuildInModels();
        loadBuildInShaders();
        loadBuildInPolygons();

        resetShader();
        setCullingBackface(true);

        textureBatch = new TextureBatch();
    }

    private void loadBuiltInTextures() {
        TextureLoader textureLoader = new TextureLoader(game);
        textureWhite = textureLoader.load(64, 64, (x, y) -> 0xFFFFFFFF);
        textureBlue = textureLoader.load(64, 64, (x, y) -> 0x0000FFFF);
        textureBlack = textureLoader.load(64, 64, (x, y) -> 0x000000FF);
        textureCheckers = textureLoader.load(64, 64, (x, y) -> (x / 16 + y / 16) % 2 == 0 ? 0xFFFFFFFF : 0x000000FF);
    }

    private void loadBuildInModels() {
        ModelLoader modelLoader = new ModelLoader(game, false);
        modelQuad = modelLoader.load(new float[]{ 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0 },
                new float[] { 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1 },
                new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 });
        modelQuadFlipped = modelLoader.load(new float[]{ 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0 },
                new float[] { 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0 },
                new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 });
        modelCube = modelLoader.load(new float[]{-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
                        0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                        0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
                        -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
                        0.5f, -0.5f, -0.5f, 0.5f, 0.5f},
                new float[]{0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1,
                        1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1},
                new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, -1, 0, 0, -1,
                        0, 0, -1, 0, 0, -1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
                        -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0});
        model2Cube = modelLoader.load(new float[]{-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, 1.0f, 1.0f},
                new float[]{0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1,
                        1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1},
                new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, -1, 0, 0, -1,
                        0, 0, -1, 0, 0, -1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
                        -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0});
    }

    private void loadBuildInShaders() {
        ShaderLoader shaderLoader = new ShaderLoader(game);
        JarResourceLocationFactory resourceLocationFactory = new JarResourceLocationFactory(getClass().getClassLoader());
        shaderDefault = shaderLoader.load("SHADER_DEFAULT", resourceLocationFactory.get("default.vert"), resourceLocationFactory.get("default.frag"));
        shaderSolid = shaderLoader.load("SHADER_SOLID", resourceLocationFactory.get("solid.vert"), resourceLocationFactory.get("solid.frag"));
        shaderTile = shaderLoader.load("SHADER_TILE", resourceLocationFactory.get("tile.vert"), resourceLocationFactory.get("tile.frag"));
    }

    private void loadBuildInPolygons() {
        int circleAccuracy = 35;
        polygonSquare = new Polygon(new Vector2f[]{new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1)});
        polygonCenteredSquare = new Polygon(new Vector2f[]{new Vector2f(-.5f, -.5f), new Vector2f(.5f, -.5f), new Vector2f(.5f, .5f), new Vector2f(-.5f, .5f)});
        polygonCircle = new Polygon(circleAccuracy, pointNum -> new Vector2f((float)Math.cos(pointNum*Math.PI*2/circleAccuracy), (float)Math.sin(pointNum*Math.PI*2/circleAccuracy)));
    }

    public void setShader(ShaderProgram shader) {
        if(!shader.equals(this.shader)) {
            if(getTextureBatch() != null) {
                getTextureBatch().flush(this);
            }
            this.shader = shader;
            GL33.glUseProgram(shader.getProgramId());
        }
    }

    public void resetShader() {
        ShaderProgram defaultShader = shaderDefault;
        setShader(defaultShader);
        GL33.glUseProgram(defaultShader.getProgramId());
    }

    public void clear(float r, float g, float b) {
        getTextureBatch().flush(this);
        GL33.glClearColor(r, g, b, 1f);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
    }

    public void setCullingBackface(boolean cullingBackface) {
        if(this.cullingBackface != cullingBackface) {
            if (cullingBackface) {
                GL33.glEnable(GL33.GL_CULL_FACE);
            } else {
                GL33.glDisable(GL33.GL_CULL_FACE);
            }
        }
        this.cullingBackface = cullingBackface;
    }

    public static void errorCheck() {
        int error = GL33.glGetError();
        if (error != 0) {
            log.error("OpenGL error occurred: " + error);
            assert false;
        }
    }

    public Vector4fc getColor() {
        return color;
    }

    public void setColor(float r, float g, float b, float alpha) {
        getTextureBatch().flush(this);
        color.set(r, g, b, alpha);
    }

    public void drawRectangle(float x, float y, float width, float height) {
        polygonSquare.setScale(width, height);
        polygonSquare.getColor().set(color);
        polygonSquare.render(this, x, y);
    }

    public void drawRectangleOutline(float x, float y, float width, float height) {
        polygonSquare.setScale(width, height);
        polygonSquare.getColor().set(color);
        polygonSquare.setAngle(0f);
        polygonSquare.renderOutline(this, x, y);
    }

    public void drawCircle(float x, float y, float radius) {
        polygonCircle.setScale(radius, radius);
        polygonCircle.getColor().set(color);
        polygonCircle.setAngle(0f);
        polygonCircle.render(this, x, y);
    }

    public void drawCircleOutline(float x, float y, float radius) {
        polygonCircle.setScale(radius, radius);
        polygonCircle.getColor().set(color);
        polygonCircle.renderOutline(this, x, y);
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        drawLine(x1, y1, x2, y2, 1f);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float width) {
        float angle = (float)Math.atan2(y2 - y1, x2 - x1);
        float x = x1 + (x2 - x1)/2f;
        float y = y1 + (y2 - y1)/2f;
        float len = MathUtils.distance(x1, y1, x2, y2);

        polygonCenteredSquare.setScaleX(len + width);
        //noinspection SuspiciousNameCombination
        polygonCenteredSquare.setScaleY(width);
        polygonCenteredSquare.setAngle(angle);
        polygonCenteredSquare.getColor().set(color);
        polygonCenteredSquare.render(this, x, y);
    }

    /**
     * Draws a quad. The only uniforms that are populated will be the model, view and projection matrix, based off
     * of the shape of the quad and the current view. Useful for implementing your own shaders.
     * @param x x position of quad (world space)
     * @param y y position of quad (world space)
     * @param width width of quad
     * @param height height of quad
     */
    public void drawQuad(float x, float y, float width, float height) {
        getTextureBatch().flush(this);

        matrix4f = matrix4f.identity()
                .translate(x, y, 0)
                .scale(width, height, 1);

        GL33.glDisable(GL33.GL_DEPTH_TEST);

        //Populate uniforms
        ShaderProgram shader = getShader();

        shader.setUniform(ShaderUniform.MODEL, matrix4f);
        shader.setUniform(ShaderUniform.VIEW, getView().getViewMatrix());
        shader.setUniform(ShaderUniform.PROJECTION, getView().getProjectionMatrix());

        //Bind vertex array
        GL33.glBindVertexArray(modelQuad.getMeshes().get(0).getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, modelQuad.getMeshes().get(0).getVertexCount());
        shader.resetBoundTextures(this);
    }

    /**
     * Draws a fullscreen quad. The only uniforms that are populated will be the model, view and projection matrix,
     * based off of the current view. Useful for implementing your own shaders.
     */
    public void drawFullscreenQuad() {
        drawQuad(getView().getX(), getView().getY(), getView().getWidth(), getView().getHeight());
    }
}
