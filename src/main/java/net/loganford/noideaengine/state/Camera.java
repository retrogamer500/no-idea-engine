package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.shape.Cuboid;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera extends AbstractViewProjection {
    @Getter private Vector3f position = new Vector3f();
    @Getter @Setter private Vector3f focus = new Vector3f();
    @Getter @Setter private Vector3f up = new Vector3f(0, 1, 0);
    @Getter @Setter private float fov;

    private int passFrustum = 0;
    private int frustumTestNum = 0;

    private FrustumIntersection frustum;
    private static Matrix4f MAT4 = new Matrix4f();
    private static Matrix4f MAT4_2 = new Matrix4f();
    private static Vector3f V3F = new Vector3f();

    public Camera(Game game, GameState gameState) {
        this(game, gameState, (float)Math.toRadians(75));
    }

    public Camera(Game game, GameState gameState, float fov) {
        super(game, gameState);
        this.fov = fov;
        frustum = new FrustumIntersection();
    }

    @Override
    protected void beforeRender(GameState gameState) {

        /*System.out.println("Frustum test pass percent: " + ((float) passFrustum) / frustumTestNum);*/
        passFrustum = 0;
        frustumTestNum = 0;

        float aspect = ((float) game.getWindow().getWidth()) / game.getWindow().getHeight();
        projectionMatrix.identity().perspective(fov, aspect, .05f, 5000f).lookAt(0, 0, 0, focus.x - position.x, focus.y - position.y, focus.z - position.z, up.x, up.y, up.z);
        viewMatrix.identity().translate(-position.x, -position.y, -position.z);
        frustum.set(MAT4.set(projectionMatrix).mul(viewMatrix));
        //frustum.set(MAT4.identity().translate(-position.x, -position.y, -position.z).mul(projectionMatrix));
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

    public boolean testBoundingBox(Cuboid box) {
        frustumTestNum++;

        assert(box.getSize().x() > 0);
        assert(box.getSize().y() > 0);
        assert(box.getSize().z() > 0);

        boolean retVal = frustum.testAab(box.getPosition(), V3F.set(box.getPosition()).add(box.getSize()));

        if(retVal) {
            passFrustum++;
        }

        return retVal;
    }
}
