package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera extends AbstractViewProjection {
    @Getter private Vector3f position = new Vector3f();
    @Getter @Setter private Vector3f focus = new Vector3f();
    @Getter @Setter private Vector3f up = new Vector3f(0, 1, 0);
    @Getter @Setter private float fov;

    public Camera(Game game, GameState gameState) {
        this(game, gameState, (float)Math.toRadians(75));
    }

    public Camera(Game game, GameState gameState, float fov) {
        super(game, gameState);

        this.fov = fov;
    }

    @Override
    protected void beforeRender(GameState gameState) {
        float aspect = ((float) game.getWindow().getWidth()) / game.getWindow().getHeight();
        projectionMatrix.identity().perspective(fov, aspect, .05f, 5000f).lookAtPerspective(0, 0, 0, focus.x - position.x, focus.y - position.y, focus.z - position.z, up.x, up.y, up.z, projectionMatrix);
        viewMatrix.identity().translate(-position.x, -position.y, -position.z);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void lookAt(Vector3fc destination) {
        focus.set(destination);
    }

    public void lookAt(float x, float y, float z) {
        focus.set(x, y, z);
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }
}
