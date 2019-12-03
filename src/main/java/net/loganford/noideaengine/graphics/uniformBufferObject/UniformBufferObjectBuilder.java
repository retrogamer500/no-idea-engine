package net.loganford.noideaengine.graphics.uniformBufferObject;


import net.loganford.noideaengine.GameEngineException;

import java.util.ArrayList;
import java.util.List;

public class UniformBufferObjectBuilder {
    private List<UniformBufferObjectUniform> bufferedUniforms;
    private int index = 0;
    private boolean inArray = false;
    private boolean inStruct = false;

    public UniformBufferObjectBuilder() {
        reset();
    }

    public UniformBufferObjectBuilder put(UniformBufferObjectUniform ubou) {
        align(ubou.getAlignment());
        ubou.setLocation(index);
        bufferedUniforms.add(ubou);
        index+= ubou.getSize();
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
        align(16);
        return this;
    }

    public UniformBufferObjectBuilder beginStruct() {
        if(inStruct) {
            throw new GameEngineException("Nested structs are not supported for UBOs");
        }
        inStruct = true;
        align(16);
        return this;
    }

    public UniformBufferObjectBuilder endStruct() {
        inStruct = false;
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

    private void align(int amount) {
        while(index % amount != 0) {
            index++;
        }
    }
}
