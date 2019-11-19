package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.shape.AbstractCompoundShape;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Matrix4fc;

import java.util.*;
import java.util.stream.Collectors;

public class Model extends Resource implements UnsafeMemory {

    @Getter private List<Mesh> meshes = new ArrayList<>();

    public void init() {
        for(Mesh mesh: meshes) {
            mesh.init(false);
        }

        meshes.sort(Comparator.comparing(Mesh::getMaterial));
    }

    @Override
    public void freeMemory() {
        for(Mesh mesh: meshes) {
            mesh.freeMemory();
        }
    }

    public void render(Renderer renderer, Matrix4fc matrix) {
        boolean popShader = false;
        if(renderer.getShader() == null) {
            renderer.pushShader(renderer.getModelShader());
            popShader = true;
        }

        for(Mesh mesh: meshes) {
            mesh.render(renderer, matrix);
        }

        if(popShader) {
            renderer.popShader();
        }
    }

    public void render(Renderer renderer, float x, float y) {
        boolean popShader = false;
        if(renderer.getShader() == null) {
            renderer.pushShader(renderer.getModelShader());
            popShader = true;
        }

        for(Mesh mesh: meshes) {
            mesh.render(renderer, x, y);
        }

        if(popShader) {
            renderer.popShader();
        }
    }

    public void render(Renderer renderer, float x, float y, float z) {
        boolean popShader = false;
        if(renderer.getShader() == null) {
            renderer.pushShader(renderer.getModelShader());
            popShader = true;
        }

        for(Mesh mesh: meshes) {
            mesh.render(renderer, x, y, z);
        }

        if(popShader) {
            renderer.popShader();
        }
    }

    public int getVertexCount() {
        int count = 0;
        for(Mesh mesh: meshes) {
            count+= mesh.getVertexCount();
        }
        return count;
    }

    public ModelShape getShape() {
        return new ModelShape(this);
    }

    public static class ModelShape extends AbstractCompoundShape {
        private Model model;

        public ModelShape(Model model) {
            this.model = model;
        }

        @Override
        public Iterator<Shape> iterator() {
            return new Iterator<Shape>() {
                List<Iterator<Shape>> iteratorList = model.meshes.stream().map((mesh) -> mesh.getShape().iterator()).collect(Collectors.toList());

                @Override
                public boolean hasNext() {
                    while(iteratorList.size() > 0 && !iteratorList.get(0).hasNext()) {
                        iteratorList.remove(0);
                    }

                    return iteratorList.size() > 0;
                }

                @Override
                public Shape next() {
                    while(iteratorList.size() > 0 && !iteratorList.get(0).hasNext()) {
                        iteratorList.remove(0);
                    }

                    return iteratorList.get(0).next();
                }
            };
        }
    }
}
