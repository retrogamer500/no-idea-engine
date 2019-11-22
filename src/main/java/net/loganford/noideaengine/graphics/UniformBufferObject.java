package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class UniformBufferObject implements UnsafeMemory {
    private List<Pair<Integer, Object>> bufferedUniforms;
    private ByteBuffer buffer;
    @Getter private int id;

    UniformBufferObject(List<Pair<Integer, Object>> bufferedUniforms, int size) {
        UnsafeMemoryTracker.track(this);
        this.bufferedUniforms = bufferedUniforms;
        buffer = MemoryUtil.memAlloc(size);

        id = GL33.glGenBuffers();
    }

    public void buffer() {
        for(Pair<Integer, Object> pair : bufferedUniforms) {
            int location = pair.getLeft();
            Object object = pair.getRight();

            if(object instanceof Vector3f) {
                Vector3f vector3f = (Vector3f) object;
                buffer.position(location).putFloat(vector3f.x).putFloat(vector3f.y).putFloat(vector3f.z);
            }
            else if(object instanceof Vector4f) {
                Vector4f vector4f = (Vector4f) object;
                buffer.position(location).putFloat(vector4f.x).putFloat(vector4f.y).putFloat(vector4f.z).putFloat(vector4f.w);
            }
            else if(object instanceof Boolean) {
                Boolean value = (Boolean) object;
                buffer.position(location).put(value ? (byte)1 : (byte)0);
            }
            else if(object instanceof Float) {
                Float value = (Float) object;
                buffer.position(location).putFloat(value);
            }
            else if(object instanceof Integer) {
                Integer value = (Integer) object;
                buffer.position(location).putInt(value);
            }
            else if(object instanceof Matrix4f) {
                Matrix4f matrix4f = (Matrix4f) object;
                buffer.clear();
                matrix4f.get(location, buffer);
            }
        }

        buffer.clear();
        GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, id);
        GL33.glBufferData(GL33.GL_UNIFORM_BUFFER, buffer, GL33.GL_DYNAMIC_DRAW);
    }

    @Override
    public void freeMemory() {
        UnsafeMemoryTracker.untrack(this);
        MemoryUtil.memFree(buffer);
    }
}
