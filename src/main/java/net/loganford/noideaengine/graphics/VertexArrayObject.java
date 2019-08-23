package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class VertexArrayObject implements UnsafeMemory {
    @Getter private int id;
    private List<VertexBufferObject> vertexBufferObjects;

    public VertexArrayObject() {
        id = GL33.glGenVertexArrays();
        vertexBufferObjects = new ArrayList<>();
        UnsafeMemoryTracker.track(this);
    }

    public VertexBufferObject addVertexBufferObject(int size, int vertices, int usage) {
        GL33.glBindVertexArray(id);
        VertexBufferObject vbo = new VertexBufferObject(vertexBufferObjects.size(), size, vertices, usage);
        GL33.glEnableVertexAttribArray(vertexBufferObjects.size());
        vertexBufferObjects.add(vbo);
        return vbo;
    }

    public VertexBufferObject addVertexBufferObject(int size, int vertices) {
        return addVertexBufferObject(size, vertices, GL33.GL_STATIC_DRAW);
    }

    /**
     * Flips and buffers all VBOs
     */
    public void flipAndBuffer() {
        for(VertexBufferObject vbo : vertexBufferObjects) {
            vbo.flip();
            vbo.buffer();
        }
    }

    public void buffer() {
        for(VertexBufferObject vbo : vertexBufferObjects) {
            vbo.buffer();
        }
    }

    public void flipAndUpdate(int offset) {
        for(VertexBufferObject vbo : vertexBufferObjects) {
            vbo.flip();
            vbo.update(offset);
        }
    }

    public void clear() {
        for(VertexBufferObject vbo : vertexBufferObjects) {
            vbo.clear();
        }
    }

    public void freeMemory() {
        // Bind everything
        GL33.glBindVertexArray(id);

        for(int index = 0; index < vertexBufferObjects.size(); index++) {
            GL33.glDisableVertexAttribArray(index);
            vertexBufferObjects.get(index).freeMemory();
        }

        // Unbind and delete VAO
        vertexBufferObjects = null;
        GL33.glBindVertexArray(0);
        GL33.glDeleteVertexArrays(id);
        UnsafeMemoryTracker.untrack(this);
    }


    public class VertexBufferObject implements UnsafeMemory {
        @Getter private int id;
        @Getter private int location;
        @Getter private int size;
        @Getter private int vertices;
        private int usage;
        private FloatBuffer buffer;

        /**
         * Private constructor-- should only be constructed by a VAO
         * @param location
         * @param size
         * @param vertices
         */
        private VertexBufferObject(int location, int size, int vertices, int usage) {
            this.location = location;
            this.size = size;
            this.vertices = vertices;
            this.usage = usage;

            //Create VBO
            id = GL33.glGenBuffers();
            buffer = MemoryUtil.memAllocFloat(size * vertices);
        }

        public void buffer() {
            //Send data and set attribute pointer
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, id);
            GL33.glVertexAttribPointer(location, size, GL33.GL_FLOAT, false, 0, 0);
            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, buffer, usage);

            //Unbind buffer
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);

            //If usage is static, freeMemory buffer from RAM
            if(usage == GL33.GL_STATIC_DRAW || usage == GL33.GL_STATIC_READ || usage == GL33.GL_STATIC_COPY) {
                destroyBuffer();
            }
        }

        public void update(int offset) {
            if(usage != GL33.GL_STATIC_DRAW && usage != GL33.GL_STATIC_READ && usage != GL33.GL_STATIC_COPY) {
                //Update VBO
                GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, id);
                GL33.glBufferSubData(GL33.GL_ARRAY_BUFFER , offset, buffer);

                //Unbind buffer
                GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
            }
            else {
                throw new GameEngineException("Tried to update a static VBO");
            }
        }

        public void freeMemory() {
            //Delete buffer
            destroyBuffer();
            // Unbind and delete VBO
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
            GL33.glDeleteBuffers(id);
        }

        private void destroyBuffer() {
            if(buffer != null) {
                MemoryUtil.memFree(buffer);
                buffer = null;
            }
        }

        public void put(Vector4f vector) {
            buffer.put(vector.x);
            buffer.put(vector.y);
            buffer.put(vector.z);
            buffer.put(vector.w);
        }

        public void put(Vector3f vector) {
            buffer.put(vector.x);
            buffer.put(vector.y);
            buffer.put(vector.z);
        }

        public void put(Vector2f vector) {
            buffer.put(vector.x);
            buffer.put(vector.y);
        }

        public void put(float f) {
            buffer.put(f);
        }

        public void flip() {
            buffer.flip();
        }

        public void clear() {
            buffer.clear();
        }

        public void rewind() {
            buffer.rewind();
        }
    }

}
