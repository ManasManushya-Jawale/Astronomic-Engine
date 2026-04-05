package org.astroEngine.graphics.shaders;

import org.astroEngine.events.Shaders.ShaderProgramAdapter;
import org.astroEngine.events.Shaders.ShaderProgramListener;
import org.astroEngine.graphics.Shape;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class PlainShader extends Shape {

    private int shaderProgram;
    private int VAO;

    private final String vertexSrc;
    private final String fragmentSrc;

    private ShaderProgramListener shaderProgramListener = new ShaderProgramAdapter();

    public PlainShader(String vertexSrc, String fragmentSrc) {
        super(Color.WHITE);
        this.vertexSrc = vertexSrc;
        this.fragmentSrc = fragmentSrc;
    }

    public void compile() {
        float[] vertices = new float[]{

        };
        // ===== VAO + VBO =====
        VAO = glGenVertexArrays();
        int VBO = glGenBuffers();

        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        // position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // ===== SHADER =====
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vertexSrc);
        glCompileShader(vs);

        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fragmentSrc);
        glCompileShader(fs);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vs);
        glAttachShader(shaderProgram, fs);
        glLinkProgram(shaderProgram);

        // cleanup
        glDeleteShader(vs);
        glDeleteShader(fs);

    }

    @Override
    public void draw(Matrix4d transform) {
        glUseProgram(shaderProgram);
        glBindVertexArray(VAO);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        transform.get(buffer);

        shaderProgramListener.applyParams(shaderProgram);

    }

    public int getShaderProgram() {

        return shaderProgram;
    }

    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}