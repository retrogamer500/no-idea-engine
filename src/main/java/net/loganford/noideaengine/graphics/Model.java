package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.shape.AbstractCompoundShape;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Matrix4fc;

import java.util.ArrayList;
import java.util.List;

public class Model extends Resource implements UnsafeMemory {

    @Getter private List<Mesh> meshes = new ArrayList<>();

    public void init() {
        for(Mesh mesh: meshes) {
            mesh.init(false);
        }
    }

    @Override
    public void freeMemory() {
        for(Mesh mesh: meshes) {
            mesh.freeMemory();
        }
    }

    public void render(Renderer renderer, Matrix4fc matrix) {
        for(Mesh mesh: meshes) {
            mesh.render(renderer, matrix);
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

    public Shape getShape() {
        return new ModelShape(this);
    }

    private static class ModelShape extends AbstractCompoundShape {
        private Model model;

        public ModelShape(Model model) {
            this.model = model;
        }


        @Override
        public List<? extends Shape> getShapes() {
            List<Face> shapeList = new ArrayList<>();

            for(Mesh mesh : model.getMeshes()) {
                shapeList.addAll(mesh.getFaces());
            }

            return shapeList;
        }
    }
}
