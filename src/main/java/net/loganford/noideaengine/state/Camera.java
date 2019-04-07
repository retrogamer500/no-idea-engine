package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends ScreenTransformation {
    @Getter @Setter private Vector3f position;
    @Getter @Setter private Vector3f focus;
    @Getter @Setter private Vector3f up;
    @Getter @Setter private float fov;

    public Camera(Game game, GameState gameState) {
        this(game, gameState, (float)Math.toRadians(90));
    }

    public Camera(Game game, GameState gameState, float fov) {
        super(game, gameState);

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f().identity();

        position = new Vector3f();
        focus = new Vector3f();
        up = new Vector3f(0, 1, 0);

        this.fov = fov;
    }

    @Override
    protected void step() {

    }

    protected void calculateViewProjection() {
        float aspect = ((float) game.getWindow().getWidth()) / game.getWindow().getHeight();
        projectionMatrix.identity().perspective(fov, aspect, .05f, 5000f).lookAtPerspective(0, 0, 0, focus.x - position.x, focus.y - position.y, focus.z - position.z, up.x, up.y, up.z, projectionMatrix);
        viewMatrix.identity().translate(-position.x, -position.y, -position.z);
    }

    public void setPostition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void lookAt(Vector3f destination) {
        focus.set(destination);
    }

    public void lookAt(float x, float y, float z) {
        focus.set(x, y, z);
    }
}
