package net.loganford.noideaengine.graphics.shader;

public class VertexShader {
	private String fileName;
	private int shaderId;

	public VertexShader(String fileName, int shaderId) {
		this.fileName = fileName;
		this.shaderId = shaderId;
	}

	public String getFileName() {
		return fileName;
	}

	public int getShaderId() {
		return shaderId;
	}
}
