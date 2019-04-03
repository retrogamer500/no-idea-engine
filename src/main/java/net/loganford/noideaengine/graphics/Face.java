package net.loganford.noideaengine.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Winding order by default is CCW
 * 
 * @author Logan
 *
 */
public class Face {
	private Vector3f[] position = new Vector3f[3];
	private Vector3f[] normal = new Vector3f[3];
	private Vector2f[] uv = new Vector2f[3];
	private Vector3f[] color = new Vector3f[3];

	public Vector3f[] getPosition() {
		return position;
	}

	public void setPosition(Vector3f[] position) {
		this.position = position;
	}

	public Vector3f[] getNormal() {
		return normal;
	}

	public void setNormal(Vector3f[] normal) {
		this.normal = normal;
	}

	public Vector2f[] getUv() {
		return uv;
	}

	public void setUv(Vector2f[] uv) {
		this.uv = uv;
	}

	public Vector3f[] getColor() {
		return color;
	}

	public void setColor(Vector3f[] color) {
		this.color = color;
	}
}
