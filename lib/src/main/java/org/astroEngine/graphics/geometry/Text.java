package org.astroEngine.graphics.geometry;

import org.astroEngine.events.Shaders.ShaderProgramAdapter;
import org.astroEngine.graphics.shaders.VertexShader;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Text extends VertexShader {

    private static final int BITMAP_W = 512;
    private static final int BITMAP_H = 512;
    private static final int FIRST_CHAR = 32;
    private static final int NUM_CHARS = 96; // ASCII 32–127

    private final String text;
    private final File fontFile;

    private STBTTBakedChar.Buffer charData;
    private int texID = -1;
    private int vertexCount;
    private float[] uv;

    public float line_height = 1;
    public float FONT_SIZE = 48.0f;

    public Text(String text, File fontFile) {
        super(
                // Vertex shader
                """
                        #version 330 core
                        
                        layout (location = 0) in vec3 aPos;
                        layout (location = 1) in vec2 aTexCoord;
                        
                        out vec2 texCoord;
                        
                        uniform mat4 transform;
                        
                        void main() {
                            texCoord = aTexCoord;
                            gl_Position = transform * vec4(aPos, 1.0);
                        }
                        """,
                // Fragment shader
                """
                        #version 330 core
                        
                        in vec2 texCoord;
                        out vec4 FragColor;
                        
                        uniform sampler2D tex;
                        uniform vec4 color;
                        
                        void main() {
                            float alpha = texture(tex, texCoord).r;
                            FragColor = vec4(color.rgb, color.a * alpha);
                        }
                        """
        );

        setShaderProgramListener(new ShaderProgramAdapter() {
            // In Text.java, change applyParams to:
            @Override
            public int applyParams(int shaderProgram) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texID);
                glUniform1i(glGetUniformLocation(shaderProgram, "tex"), 0);
                glUniform4f(glGetUniformLocation(shaderProgram, "color"), 1, 1, 1, 1f); // fixed alpha too
                return shaderProgram;
            }
        });

        this.text = text;
        this.fontFile = fontFile;
    }

    @Override
    public void compile() {
        initFontTexture();
        float[] verts = buildTextMesh();
        setVerticesArr(verts);
        super.compile();

        int uvVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, uvVBO);
        glBufferData(GL_ARRAY_BUFFER, uv, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void draw(Matrix4d transform) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        super.draw(transform);
        glDisable(GL_BLEND);
    }

    /**
     * Loads the TTF font, bakes it into a bitmap, and uploads it to the GPU as a texture.
     */
    private void initFontTexture() {
        // --- Load font file ---
        ByteBuffer fontBuffer;
        try {
            byte[] bytes = Files.readAllBytes(fontFile.toPath());
            fontBuffer = MemoryUtil.memAlloc(bytes.length);
            fontBuffer.put(bytes);
            fontBuffer.flip();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font file: " + fontFile.getPath(), e);
        }

        // --- Bake font bitmap ---
        ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
        charData = STBTTBakedChar.malloc(NUM_CHARS);

        int result = STBTruetype.stbtt_BakeFontBitmap(
                fontBuffer,
                FONT_SIZE,
                bitmap,
                BITMAP_W,
                BITMAP_H,
                FIRST_CHAR,
                charData
        );

        MemoryUtil.memFree(fontBuffer);

        if (result <= 0) {
            MemoryUtil.memFree(bitmap);
            throw new RuntimeException("stbtt_BakeFontBitmap failed — try increasing BITMAP_W/H or reducing FONT_SIZE.");
        }

        // --- Upload bitmap to GPU ---
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // One-channel (red) texture for font alpha
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RED,
                BITMAP_W,
                BITMAP_H,
                0,
                GL_RED,
                GL_UNSIGNED_BYTE,
                bitmap
        );

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);

        MemoryUtil.memFree(bitmap);
    }

    /**
     * Builds a triangle mesh (two triangles per glyph) for the full text string.
     * Layout per vertex: [x, y, z,  s, t]  (5 floats)
     */
    private float[] buildTextMesh() {
        float[] verts = new float[text.length() * 6 * 3]; // 3 floats per vertex (xyz only)
        ArrayList<Float> uvMap = new ArrayList<>();
        int idx = 0;

        FloatBuffer xb = MemoryUtil.memAllocFloat(1);
        FloatBuffer yb = MemoryUtil.memAllocFloat(1);
        xb.put(0, 0.0f);
        yb.put(0, 0.0f);

        STBTTAlignedQuad q = STBTTAlignedQuad.malloc();

        for (char c : text.toCharArray()) {
            if (c == '\n') {
                xb.put(0, 0.0f);                          // reset x
                yb.put(0, yb.get(0)+FONT_SIZE);        // move down
                continue;
            }
            if (c < FIRST_CHAR || c >= FIRST_CHAR + NUM_CHARS) continue;

            STBTruetype.stbtt_GetBakedQuad(
                    charData, BITMAP_W, BITMAP_H,
                    c - FIRST_CHAR, xb, yb, q, true
            );

            float scale = 1.0f / FONT_SIZE * line_height;
            float x0 = q.x0() * scale;
            float y0 = -q.y0() * scale;
            float x1 = q.x1() * scale;
            float y1 = -q.y1() * scale;

            float s0 = q.s0(), t0 = q.t0();
            float s1 = q.s1(), t1 = q.t1();

            // Triangle 1: top-left, top-right, bottom-right
            verts[idx++] = x0; verts[idx++] = y0; verts[idx++] = 0f;
            verts[idx++] = x1; verts[idx++] = y0; verts[idx++] = 0f;
            verts[idx++] = x1; verts[idx++] = y1; verts[idx++] = 0f;

            // Triangle 2: top-left, bottom-right, bottom-left
            verts[idx++] = x0; verts[idx++] = y0; verts[idx++] = 0f;
            verts[idx++] = x1; verts[idx++] = y1; verts[idx++] = 0f;
            verts[idx++] = x0; verts[idx++] = y1; verts[idx++] = 0f;

            // UVs matching the same vertex order
            uvMap.add(s0); uvMap.add(t0);
            uvMap.add(s1); uvMap.add(t0);
            uvMap.add(s1); uvMap.add(t1);

            uvMap.add(s0); uvMap.add(t0);
            uvMap.add(s1); uvMap.add(t1);
            uvMap.add(s0); uvMap.add(t1);
        }

        q.free();
        MemoryUtil.memFree(xb);
        MemoryUtil.memFree(yb);

        vertexCount = idx / 3; // 3 floats per vertex

        uv = new float[uvMap.size()];
        for (int i = 0; i < uv.length; i++) uv[i] = uvMap.get(i);

        return Arrays.copyOf(verts, idx);
    }

    /**
     * Bind the font texture before drawing.
     */
    public void bindTexture() {
        if (texID != -1) {
            glBindTexture(GL_TEXTURE_2D, texID);
        }
    }

    /**
     * Returns the number of vertices in the mesh (useful for glDrawArrays).
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Free all native resources. Call this when the Text object is no longer needed.
     */
    public void cleanup() {
        if (charData != null) {
            charData.free();
            charData = null;
        }
        if (texID != -1) {
            glDeleteTextures(texID);
            texID = -1;
        }
    }
}