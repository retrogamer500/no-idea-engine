package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.shape.Cuboid;
import net.loganford.noideaengine.shape.Shape;
import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Face extends Shape {
	private static Vector3f V3F = new Vector3f();

	@Getter @Setter private Vector3f[] positions = new Vector3f[3];
	@Getter @Setter private Vector3f[] normals = new Vector3f[3];
	@Getter @Setter private Vector2f[] uvs = new Vector2f[3];
	@Getter @Setter private Vector3f[] colors = new Vector3f[3];

	public Face() {

    }

	public Face(Vector3f v0, Vector3f v1, Vector3f v2) {
	    positions[0] = new Vector3f(v0);
	    positions[1] = new Vector3f(v1);
	    positions[2] = new Vector3f(v2);
    }

	@Override
	public void getPosition(Vector3f position) {
		position.set(positions[0]);
	}

	@Override
	public void setPosition(Vector3fc position) {
        Vector3f difference = V3F.set(positions[0]).sub(position);
        positions[0].add(difference);
        positions[1].add(difference);
        positions[2].add(difference);
	}

	@Override
	public void getBoundingBox(Cuboid cube) {
        cube.setX(NumberUtils.min(positions[0].x, positions[1].x, positions[2].x));
        cube.setY(NumberUtils.min(positions[0].y, positions[1].y, positions[2].y));
        cube.setZ(NumberUtils.min(positions[0].z, positions[1].z, positions[2].z));

        cube.setWidth(NumberUtils.max(positions[0].x, positions[1].x, positions[2].x) - cube.getX());
        cube.setHeight(NumberUtils.max(positions[0].y, positions[1].y, positions[2].y) - cube.getY());
        cube.setDepth(NumberUtils.max(positions[0].z, positions[1].z, positions[2].z) - cube.getZ());
	}

	public Vector3f getV0() {
	    return positions[0];
    }

    public Vector3f getV1() {
        return positions[1];
    }

    public Vector3f getV2() {
        return positions[2];
    }

    public void setV0(Vector3f v0) {
	    positions[0].set(v0);
    }

    public void setV1(Vector3f v1) {
        positions[1].set(v1);
    }

    public void setV2(Vector3f v2) {
        positions[2].set(v2);
    }
}
