package org.astroEngine.graphics;

import org.astroEngine.events.ShaderProgramListener;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL40.glUniformMatrix4dv;

import static org.astroEngine.util.ShaderUtils.VertexInfo;

public class ShaderSprite extends Shape {

    int vertexShader;
    int fragmentShader;
    int shaderProgram;

    private String vertexShaderSource;
    private String fragmentShaderSource;

    private int transformLoc;
    VertexInfo info;

    public List<Float> vertices;
    public int SHAPE_TYPE;

    private float[] verticesArr;

    private ShaderProgramListener shaderProgramListener = i -> {
        return i;
    };

    public ShaderSprite(String vertexShader, String fragmentShader, List<Float> vertices) {
        super(Color.WHITE);

        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;
        this.vertices = vertices;

        this.SHAPE_TYPE = GL_TRIANGLE_FAN;

        this.verticesArr = new float[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            this.verticesArr[i] = vertices.get(i);
        }
    }

    public ShaderSprite(int SHAPE_TYPE, List<Float> vertices, String vertexShaderSource, String fragmentShaderSource) {
        super(Color.WHITE);
        this.SHAPE_TYPE = SHAPE_TYPE;
        this.vertices = vertices;
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        this.verticesArr = new float[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            this.verticesArr[i] = vertices.get(i);
        }


    }   @Override
    public void draw(Matrix4d transform) {

        int VAO = info.VAO();
        int len = info.len();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        transform.get(buffer);

        transformLoc = glGetUniformLocation(shaderProgram, "transform");

        glBindVertexArray(VAO);
        glUseProgram(shaderProgram);
        applyShaderProgramListener(shaderProgramListener);
        glUniformMatrix4fv(transformLoc, false, buffer);
        glDrawArrays(SHAPE_TYPE, 0, len);

        // Reset shaders to 0 (default)
        glUseProgram(0);
    }

    public void applyShader() {
        glBindVertexArray(info.VAO());
        glUseProgram(shaderProgram);
    }

    public void compile() {
        try {
            createShader(createVertexAttr());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VertexInfo createVertexAttr() {
        for (int i = 0; i < verticesArr.length; i++) {
            verticesArr[i] = vertices.get(i);
        }

        int len = verticesArr.length / 3;

        int VBO = glGenBuffers();

        int VAO = glGenVertexArrays();

        glBindVertexArray(VAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, verticesArr, GL_STATIC_DRAW);

// 3. then set our vertex attributes pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        return (info = new VertexInfo(VAO, len));
    }

    private void createShader(VertexInfo info) throws URISyntaxException, IOException {
        int VAO = info.VAO();
        int len = info.len();

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        // Sourcing
        glShaderSource(vertexShader, vertexShaderSource);
        glShaderSource(fragmentShader, fragmentShaderSource);

        // Compiling
        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);

        // Attaching into a shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram); // Validating if there are any errors

        transformLoc = glGetUniformLocation(shaderProgram, "transform");

        int compileStatus = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (compileStatus == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(vertexShader));
        }

        compileStatus = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (compileStatus == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(fragmentShader));
        }

    }

    public String getFragmentShaderSource() {
        return fragmentShaderSource;
    }

    public void setFragmentShaderSource(String fragmentShaderSource) {
        this.fragmentShaderSource = fragmentShaderSource;
        compile();
    }

    public String getVertexShaderSource() {
        return vertexShaderSource;
    }

    public void setVertexShaderSource(String vertexShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
        compile();
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public int getFragmentShader() {
        return fragmentShader;
    }

    public void setFragmentShader(int fragmentShader) {
        this.fragmentShader = fragmentShader;
    }

    public int getVertexShader() {
        return vertexShader;
    }

    public void setVertexShader(int vertexShader) {
        this.vertexShader = vertexShader;
    }

    public void applyShaderProgramListener(ShaderProgramListener listener) {
        setShaderProgram(listener.applyParams(shaderProgram));
    }

    public ShaderProgramListener getShaderProgramListener() {
        return shaderProgramListener;
    }

    public void setShaderProgramListener(ShaderProgramListener shaderProgramListener) {
        this.shaderProgramListener = shaderProgramListener;
    }
}
