package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import org.joml.Vector4f;

public class View extends ScreenTransformation {
    private static Vector4f V4 = new Vector4f();

    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private int width;
    @Getter @Setter private int height;

    public View(Game game, GameState gameState, int width, int height) {
        super(game, gameState);

        this.width = width;
        this.height = height;
    }

    @Override
    protected void calculateViewProjection() {
        float difference = 0;
        if(!gameState.isStretch()) {
            difference = game.getWindow().getHeight() - height;
        }

        projectionMatrix.identity().ortho(0, width, height, 0, -100f, 100f);
        viewMatrix.identity().translate(-x, -y - difference, 0);
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

    public double getMouseX() {
        //Todo: We should probably cache these values
        V4.set((float)getGame().getInput().getMouseX(), (float)getGame().getInput().getMouseY(), 0f, 1f);
        V4.mul(getViewMatrix().invert());
        return V4.x;
    }

    public double getMouseY() {
        V4.set((float)getGame().getInput().getMouseX(), (float)getGame().getInput().getMouseY(), 0f, 0f);
        V4.mul(getViewMatrix().invert());
        return V4.y;
    }
}
