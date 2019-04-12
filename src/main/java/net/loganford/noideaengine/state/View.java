package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class View extends ScreenTransformation {
    private static Vector4f V4 = new Vector4f();
    private static Matrix4f M4 = new Matrix4f();

    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private int width;
    @Getter @Setter private int height;
    @Getter @Setter private float zoom = 1f;
    @Getter @Setter private float angle = 0f;

    public View(Game game, GameState gameState, int width, int height) {
        super(game, gameState);

        this.width = width;
        this.height = height;
    }

    @Override
    protected void beforeRender(GameState gameState) {
        float difference = 0;
        if(!gameState.isStretch()) {
            difference = game.getWindow().getHeight() - height * gameState.getScale();
        }

        projectionMatrix.identity().ortho(0, width, height, 0, -100f, 100f);
        viewMatrix.identity().translate(width/2f, height/2f, 0f).scale(zoom, zoom, 1f).rotateZ(-angle).translate(-width/2f, -height/2f, 0f).translate(-x, -y - difference, 0);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);

        this.width = width;
        this.height = height;
    }

    public void lookAt(float x, float y) {
        this.x = x - (float)width/2;
        this.y = y - (float)height/2;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getMouseX() {
        V4.set(getGame().getInput().getMouseX(), getGame().getInput().getMouseY(), 0f, 1f);
        V4.add(-width/2f, -height/2f, 0f, 0f)
                .rotateZ(angle).mul(1/zoom)
                .add(width/2f, height/2f, 0f, 0f)
                .mul(1f/getGameState().getScale())
                .add(x, y, 0, 0);
        return V4.x;
    }

    public float getMouseY() {
        V4.set(getGame().getInput().getMouseX(), getGame().getInput().getMouseY(), 0f, 1f);
        V4.add(-width/2f, -height/2f, 0f, 0f)
                .rotateZ(angle).mul(1/zoom)
                .add(width/2f, height/2f, 0f, 0f)
                .mul(1f/getGameState().getScale())
                .add(x, y, 0, 0);
        return V4.y;
    }
}
