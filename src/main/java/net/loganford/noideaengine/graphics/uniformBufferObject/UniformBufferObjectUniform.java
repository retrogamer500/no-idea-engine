package net.loganford.noideaengine.graphics.uniformBufferObject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.GameEngineException;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class UniformBufferObjectUniform<O> {
    private O o;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) private int location = -1;

    UniformBufferObjectUniform(O o) {
        this.location = location;
        this.o = o;
    }

    public O get() {
        return o;
    }

    public void set(O o) {
        this.o = o;
    }

    public int getAlignment() {
        if(o instanceof Boolean || o instanceof Float || o instanceof Integer) {
            return 4;
        }
        else if(o instanceof Vector3f || o instanceof Vector4f || o instanceof Matrix4f) {
            return 16;
        }
        else {
            throw new GameEngineException("Invalid uniform buffer object uniform type: " + o.getClass().toString());
        }
    }

    public int getSize() {
        if(o instanceof Boolean || o instanceof Float || o instanceof Integer) {
            return 4;
        }
        else if(o instanceof Vector3f || o instanceof Vector4f) {
            return 16;
        }
        else if(o instanceof Matrix4f) {
            return 64;
        }
        else {
            throw new GameEngineException("Invalid uniform buffer object uniform type: " + o.getClass().toString());
        }
    }
}
