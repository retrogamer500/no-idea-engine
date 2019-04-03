package net.loganford.noideaengine.graphics.shader;

public class FragmentShader {
	private String fileName;
	private int shaderId;

	public FragmentShader(String fileName, int shaderId) {
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
