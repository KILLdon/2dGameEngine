package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 17.04.2017.
 */
public class ShaderProgram {

    private static Map<Integer, Shader> shaderIDs = new HashMap<>();
    private static final ShaderProgram
            entityShader,
            viewShader,
            roomShader,
            fillColorShader,
            backgroundShader,
            partShader,
            upsideDownPartShader,
            textShader;
    static {
        final String DEFAULT_PATH = Parser.packageToString(ShaderProgram.class);
        entityShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "entityFragment.glsl"),
                new String[]{
                        "transformationMatrix"
                });
        viewShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "viewFragment.glsl"),
                new String[]{
                        "transformationMatrix",
                        "point",
                        "size"
                });
        roomShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "roomFragment.glsl"),
                new String[]{
                        "transformationMatrix"
                });
        fillColorShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "fillColorVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "fillColorFragment.glsl"),
                new String[]{
                        "color"
                });
        backgroundShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "fillVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "backgroundFragment.glsl"),
                new String[]{
                        "scale"
                });
        partShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "partFragment.glsl"),
                new String[]{
                        "transformationMatrix",
                        "point",
                        "size"
                });
        upsideDownPartShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "upsideDownFragment.glsl"),
                new String[]{
                        "transformationMatrix"
                });
        textShader = new ShaderProgram(
                Parser.parseFile(DEFAULT_PATH + "partVertex.glsl"),
                Parser.parseFile(DEFAULT_PATH + "textFragment.glsl"),
                new String[]{
                        "transformationMatrix",
                        "color"
                });
    };

    int ID;
    private Map<String, Integer> uniformLocations = new HashMap<>();

    ShaderProgram(String vertex, String fragment, String[] uniformKeys) {
        Shader ids = new Shader();
        ids.vertexID = loadShader(GL20.GL_VERTEX_SHADER, vertex);
        ids.fragmentID = loadShader(GL20.GL_FRAGMENT_SHADER, fragment);
        ID = GL20.glCreateProgram();

        GL20.glAttachShader(ID, ids.vertexID);
        GL20.glAttachShader(ID, ids.fragmentID);
        GL20.glLinkProgram(ID);

        GL20.glDeleteShader(ids.vertexID);
        GL20.glDeleteShader(ids.fragmentID);
        GL20.glValidateProgram(ID);

        getUniformLocations(uniformKeys);
        shaderIDs.put(ID, ids);
    }

    static void clear() {
        for (Map.Entry<Integer, Shader> program : shaderIDs.entrySet()) {
            GL20.glDetachShader(program.getKey(), program.getValue().vertexID);
            GL20.glDetachShader(program.getKey(), program.getValue().fragmentID);
            GL20.glDeleteProgram(program.getKey());
        }
    }

    private static int loadShader(int type, String source) {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            String shaderType = "";
            switch (type) {
                case GL20.GL_VERTEX_SHADER:
                    shaderType = "vertex";
                    break;
                case GL32.GL_GEOMETRY_SHADER:
                    shaderType = "geometry";
                    break;
                case GL20.GL_FRAGMENT_SHADER:
                    shaderType = "fragment";
                    break;
            }
            System.out.println("Could not compile " + shaderType + " shader!");
            System.out.println(source);
        }
        return shaderID;
    }

    void getUniformLocations(String[] uniformKeys) {
        uniformLocations.clear();
        for (String key : uniformKeys) {
            uniformLocations.put(key, GL20.glGetUniformLocation(ID, key));
        }
    }

    void loadUniformLocation(String key, Object value) {
        if (value instanceof Matrix3f) {
            loadMatrix3f(uniformLocations.get(key), (Matrix3f)value);
        } else if (value instanceof Boolean) {
            loadBool(uniformLocations.get(key), (Boolean)value);
        } else if (value instanceof Vector2f) {
            loadVector2f(uniformLocations.get(key), (Vector2f)value);
        } else if (value instanceof Vector4f) {
            loadVector4f(uniformLocations.get(key), (Vector4f)value);
        }
    }

    private static FloatBuffer matrix3fBuffer = BufferUtils.createFloatBuffer(9);

    private void loadMatrix3f(int location, Matrix3f value) {
        value.store(matrix3fBuffer);
        matrix3fBuffer.flip();
        GL20.glUniformMatrix3(location, false, matrix3fBuffer);
    }

    private void loadVector2f(int location, Vector2f value) {
        GL20.glUniform2f(location, value.x, value.y);
    }

    private void loadVector4f(int location, Vector4f value) {
        GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    private void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    private void loadBool(int location, boolean value) {
        GL20.glUniform1i(location, value ? 1 : 0);
    }

    private static class Shader {
        private int vertexID;
        private int fragmentID;
    }

    void useThis() {
        GL20.glUseProgram(ID);
    }

    static void useNone() {
        GL20.glUseProgram(0);
    }

    static ShaderProgram getEntityShader() {
        return entityShader;
    }

    static ShaderProgram getViewShader() {
        return viewShader;
    }

    static ShaderProgram getRoomShader() {
        return roomShader;
    }

    static ShaderProgram getFillColorShader() {
        return fillColorShader;
    }

    static ShaderProgram getBackgroundShader() {
        return backgroundShader;
    }

    static ShaderProgram getPartShader() {
        return partShader;
    }

    static ShaderProgram getUpsideDownPartShader() {
        return upsideDownPartShader;
    }

    static ShaderProgram getTextShader() {
        return textShader;
    }
}