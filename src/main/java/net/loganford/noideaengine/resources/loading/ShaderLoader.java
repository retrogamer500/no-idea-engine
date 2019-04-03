package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.ShaderConfig;
import net.loganford.noideaengine.graphics.shader.FragmentShader;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.VertexShader;
import net.loganford.noideaengine.utils.FileUtils;
import org.lwjgl.opengl.GL33;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class ShaderLoader implements ResourceLoader {
    private static HashMap<String, VertexShader> vertexShaderCache = new HashMap<>();
    private static HashMap<String, FragmentShader> fragmentShaderCache = new HashMap<>();

    private List<ShaderConfig> shadersToLoad;

    @Override
    public void init(Game game, LoadingContext ctx) {
        shadersToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getShaders() != null) {
            shadersToLoad.addAll(game.getConfig().getResources().getShaders());
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        ShaderConfig description = shadersToLoad.remove(0);
        ShaderProgram program = load(description);
        game.getShaderManager().put(description.getKey(), program);
    }

    @Override
    public int getRemaining() {
        return shadersToLoad.size();
    }

    public ShaderProgram load(ShaderConfig shaderDescription) {
        return load(shaderDescription.getKey(), shaderDescription.getVert(), shaderDescription.getFrag());
    }

    public ShaderProgram load(String key, String vertexFileLocation, String fragmentFileLocation) {
        File vertexFile = new File(vertexFileLocation);
        File fragmentFile = new File(fragmentFileLocation);

        VertexShader vertexShader;
        FragmentShader fragmentShader;

        //Load vertex shader
        log.info("Loading vertex shader at: " + vertexFileLocation);
        if(vertexShaderCache.containsKey(vertexFileLocation)) {
            vertexShader = vertexShaderCache.get(vertexFileLocation);
        }
        else {
            String shaderData = FileUtils.readFileAsString(vertexFile);
            int vertexShaderId = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
            GL33.glShaderSource(vertexShaderId, shaderData);
            GL33.glCompileShader(vertexShaderId);
            validateShader(vertexShaderId);
            vertexShader = new VertexShader("filename", vertexShaderId);
            vertexShaderCache.put(vertexFileLocation, vertexShader);
        }

        //Load fragment shader
        log.info("Loading fragment shader at: " + fragmentFileLocation);
        if(fragmentShaderCache.containsKey(fragmentFileLocation)) {
            fragmentShader = fragmentShaderCache.get(fragmentFileLocation);
        }
        else {
            String shaderData = FileUtils.readFileAsString(fragmentFile);
            int fragmentShaderId = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
            GL33.glShaderSource(fragmentShaderId, shaderData);
            GL33.glCompileShader(fragmentShaderId);
            validateShader(fragmentShaderId);
            fragmentShader = new FragmentShader("filename", fragmentShaderId);
            fragmentShaderCache.put(fragmentFileLocation, fragmentShader);
        }

        //Link program
        log.info("Linking shaders...");
        int programId = GL33.glCreateProgram();
        GL33.glAttachShader(programId, vertexShader.getShaderId());
        GL33.glAttachShader(programId, fragmentShader.getShaderId());
        GL33.glLinkProgram(programId);
        GL33.glValidateProgram(programId);
        validateProgram(programId);

        return new ShaderProgram(programId, key, vertexShader, fragmentShader);
    }

    private void validateShader(int shaderId) {
        int status = GL33.glGetShaderi(shaderId, GL33.GL_COMPILE_STATUS);
        if (status == GL33.GL_FALSE) {
            log.error("Shader is not valid!");
            String reason = GL33.glGetShaderInfoLog(shaderId);
            log.error("Reason: " + reason);
            throw new GameEngineException("Shader is not valid!");
        } else {
            log.info("Shader is valid");
        }
    }

    private void validateProgram(int programId) {
        int status = GL33.glGetProgrami(programId, GL33.GL_LINK_STATUS);
        if (status == GL33.GL_FALSE) {
            log.error("Program is not valid!");
            String reason = GL33.glGetProgramInfoLog(programId);
            log.error("Reason: " + reason);
            throw new GameEngineException("Program is not valid!");
        } else {
            log.info("Program is valid");
        }
    }
}
