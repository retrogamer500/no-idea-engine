package net.loganford.noideaengine.graphics;


import net.loganford.noideaengine.GameEngineException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UniformBufferObjectBuilder {
    private List<Pair<Integer, Object>> bufferedUniforms;
    private int index = 0;
    private boolean inArray = false;
    private boolean inStruct = false;
    private boolean structInArray = false;

    public UniformBufferObjectBuilder() {
        reset();
    }

    public UniformBufferObjectBuilder putVector3f(Vector3f vector3f) {
        align(16);
        put(index, vector3f);
        index += 16;
        return this;
    }

    public UniformBufferObjectBuilder putVector4f(Vector4f vector4f) {
        align(16);
        put(index, vector4f);
        index += 16;
        return this;
    }

    public UniformBufferObjectBuilder putBoolean(Boolean bool) {
        align(inArray && !structInArray ? 16 : 4);
        put(index, bool);
        index += inArray && !structInArray ? 16 : 4;
        return this;
    }

    public UniformBufferObjectBuilder putFloat(Float value) {
        align(inArray && !structInArray ? 16 : 4);
        put(index, value);
        index += inArray && !structInArray ? 16 : 4;
        return this;
    }

    public UniformBufferObjectBuilder putInteger(Integer value) {
        align(inArray && !structInArray ? 16 : 4);
        put(index, value);
        index += inArray && !structInArray ? 16 : 4;
        return this;
    }

    public UniformBufferObjectBuilder putMatrix4f(Matrix4f matrix) {
        align(16);
        put(index, matrix);
        index += 64;
        return this;
    }

    public UniformBufferObjectBuilder beginArrayElement() {
        if(inArray) {
            throw new GameEngineException("Nested arrays are not supported for UBOs");
        }
        inArray = true;
        align(16);
        return this;
    }

    public UniformBufferObjectBuilder endArrayElement() {
        inArray = false;
        return this;
    }

    public UniformBufferObjectBuilder beginStruct() {
        if(inStruct) {
            throw new GameEngineException("Nested structs are not supported for UBOs");
        }
        if(inArray) {
            structInArray = true;
        }
        inStruct = true;
        align(16);
        return this;
    }

    public UniformBufferObjectBuilder endStruct() {
        inStruct = false;
        if(inArray) {
            structInArray = false;
        }
        align(16);
        return this;
    }

    public UniformBufferObject build() {
        UniformBufferObject ubo = new UniformBufferObject(bufferedUniforms, index);
        reset();
        return ubo;
    }

    private void reset() {
        bufferedUniforms = new ArrayList<>();
        index = 0;
    }

    private void put(int location, Object object) {
        bufferedUniforms.add(new MutablePair<>(location, object));
    }

    private void align(int amount) {
        while(index % amount != 0) {
            index++;
        }
    }
}
