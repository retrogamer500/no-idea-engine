package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;

import java.util.ArrayList;
import java.util.List;

public class Model extends Resource implements UnsafeMemory {

    private List<Mesh> meshes = new ArrayList<>();

    public void init() {
        for(Mesh mesh: meshes) {
            mesh.init(false);
        }
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    @Override
    public void freeMemory() {
        for(Mesh mesh: meshes) {
            mesh.freeMemory();
        }
    }

    public void render(Renderer renderer, float x, float y) {
        for(Mesh mesh: meshes) {
            mesh.render(renderer, x, y);
        }
    }

    public void render(Renderer renderer, float x, float y, float z) {
        for(Mesh mesh: meshes) {
            mesh.render(renderer, x, y, z);
        }
    }

    public int getVertexCount() {
        int count = 0;
        for(Mesh mesh: meshes) {
            count+= mesh.getVertexCount();
        }
        return count;
    }
}
