package org.astroEngine.graphics.shaders;

import org.astroEngine.util.VertexInfo;
import org.joml.Matrix4d;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureShader extends VertexShader {
    private int textureId;
    public String texturePath;
    public Runnable textureListener = () -> {};

    @Override
    public void draw(Matrix4d transform) {
        glActiveTexture(GL_TEXTURE0);
        bind();
        super.draw(transform);
    }

    public TextureShader(String texturePath) {
        super("""
                #version 330 core
                
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec2 aTexCoord;
                
                out vec2 texCoord;
                
                uniform mat4 transform;
                
                void main() {
                    texCoord = aTexCoord;
                    gl_Position = transform * vec4(aPos, 1.0);
                }
                """, """
                #version 330 core
                
                in vec2 texCoord;
                out vec4 FragColor;
                
                uniform sampler2D tex;
                
                void main() {
                    FragColor = texture(tex, texCoord);
                }""");

        this.texturePath =texturePath;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void cleanup() {
        glDeleteTextures(textureId);
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        textureListener.run();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getTexturePath() {
        return texturePath;
    }

    int texLoc;

    @Override
    public void compile() {
        super.compile();

        texLoc = glGetUniformLocation(getShaderProgram(), "tex");
        glUniform1i(texLoc, 0); // bind sampler to GL_TEXTURE0

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true); // 👈 also important

            ByteBuffer buf = stbi_load(texturePath, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Image file [" + texturePath + "] not loaded: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            generateTexture(width, height, buf);

            stbi_image_free(buf);
        }

    }

    @Override
    public VertexInfo createVertexAttr() {
        int stride = 5 * Float.BYTES;
        int len = getVerticesArr().length / 5;

        int VBO = glGenBuffers();
        int VAO = glGenVertexArrays();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, getVerticesArr(), GL_STATIC_DRAW);

        // Position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        // Texture coords
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        return (info = new VertexInfo(VAO, len));
    }
}
