package org.astroEngine.events.Shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;

public class TextureProgramListener extends ShaderProgramAdapter {
    private String filepath;
    private int texID;
    private int texLoc;

    public float[] uv = new float[]{0,0,0,1,1,0,1,1};

    public Runnable textureParams = () -> {};

    public TextureProgramListener(String filepath)
    {
        this.filepath = filepath;
    }

    public static void setTexture2DParameter(int param, int value) {
        glTexParameteri(GL_TEXTURE_2D, param, value);
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        texID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        textureParams.run();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public int onCompile(int shaderProgram) {

        texLoc = glGetUniformLocation(shaderProgram, "tex");
        glUniform1i(texLoc, 0); // bind sampler to GL_TEXTURE0

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true); // 👈 also important

            ByteBuffer buf = stbi_load(filepath, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Image file [" + filepath + "] not loaded: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            generateTexture(width, height, buf);

            stbi_image_free(buf);
        }
        return shaderProgram;
    }

    @Override
    public int afterCompile(int shaderProgram) {
        int uvVBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, uvVBO);
        glBufferData(GL_ARRAY_BUFFER, uv, GL_STATIC_DRAW);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        return shaderProgram;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, texID);
    }
    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    public int getID(){
        return texID;
    }

    @Override
    public void preDraw() {
        GL20.glActiveTexture(GL_TEXTURE0);
        bind();
    }

    @Override
    public void postDraw() {
        unbind();
    }
}
