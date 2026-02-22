package org.astroEngine.graphics.img;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import org.astroEngine.shapes.Shape;
import org.astroEngine.util.GameUtils;
import org.astroEngine.util.Builder.Shapes;

public class ImageSprite extends Shape {
    private String filepath;
    private ByteBuffer imageBuffer;
    private int imgWidth, imgHeight;
    private int textureId;
    private boolean loaded;
    private boolean pixelPerfect;

    // --- Constructors ---
    public ImageSprite(String filepath) {
        super(Color.white);
        this.filepath = filepath;
    }

    public ImageSprite(String filepath, boolean pixelPerfect) {
        super(Color.white);
        this.filepath = filepath;
        this.pixelPerfect = pixelPerfect;
    }

    public ImageSprite(File file) {
        super(Color.white);
        this.filepath = file.getAbsolutePath();
    }

    public ImageSprite(File file, boolean pixelPerfect) {
        super(Color.white);
        this.filepath = file.getAbsolutePath();
        this.pixelPerfect = pixelPerfect;
    }

    public ImageSprite(ByteBuffer buffer, int width, int height) {
        super(Color.white);
        this.imageBuffer = buffer;
        this.imgWidth = width;
        this.imgHeight = height;
    }

    public ImageSprite(ByteBuffer buffer, int width, int height, boolean pixelPerfect) {
        super(Color.white);
        this.imageBuffer = buffer;
        this.imgWidth = width;
        this.imgHeight = height;
        this.pixelPerfect = pixelPerfect;
    }

    public ImageSprite(BufferedImage image) {
        super(Color.white);
        this.imageBuffer = Shapes.convertToByteBuffer(image);
        this.imgWidth = image.getWidth();
        this.imgHeight = image.getHeight();
    }

    public ImageSprite() {
        super(Color.white);
    }

    public ImageSprite(BufferedImage image, boolean pixelPerfect) {
        super(Color.white);
        this.imageBuffer = Shapes.convertToByteBuffer(image);
        this.imgWidth = image.getWidth();
        this.imgHeight = image.getHeight();
        this.pixelPerfect = pixelPerfect;
    }

    // --- Texture Loader ---
    private void loadTextureIfNeeded() {
        if (loaded || textureId != 0)
            return;

        ByteBuffer data;
        boolean stbAllocated = false;

        if (filepath != null) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                STBImage.stbi_set_flip_vertically_on_load(false);
                data = STBImage.stbi_load(filepath, w, h, channels, 4);
                if (data == null) {
                    throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
                }

                imgWidth = w.get(0);
                imgHeight = h.get(0);
                stbAllocated = true;
            }
        } else {
            data = imageBuffer; // manual RGBA buffer
        }

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, imgWidth, imgHeight, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, data);

        int filter = pixelPerfect ? GL_NEAREST : GL_LINEAR;
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);

        if (stbAllocated) {
            STBImage.stbi_image_free(data);
        }

        loaded = true;
    }

    @Override
    public void draw(Matrix4d transform) {
        GameUtils.applyColor(color);
        loadTextureIfNeeded();
        if (textureId == 0)
            return;

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Isolate this sprite’s transform
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        transform.get(fb);
        glMultMatrixf(fb);

        float halfW = imgWidth / 2f;
        float halfH = imgHeight / 2f;

        glBegin(GL_QUADS);
        glTexCoord2f(0f, 0f);
        glVertex2f(-halfW, -halfH);
        glTexCoord2f(1f, 0f);
        glVertex2f(halfW, -halfH);
        glTexCoord2f(1f, 1f);
        glVertex2f(halfW, halfH);
        glTexCoord2f(0f, 1f);
        glVertex2f(-halfW, halfH);
        glEnd();

        glPopMatrix(); // restore previous matrix state
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        if (textureId != 0) {
            glDeleteTextures(textureId);
            textureId = 0;
        }
    }

    public void setPixelPerfect(boolean pixelPerfect) {
        this.pixelPerfect = pixelPerfect;
        if (textureId != 0) {
            glBindTexture(GL_TEXTURE_2D, textureId);
            int filter = pixelPerfect ? GL_NEAREST : GL_LINEAR;
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    public void setImage(ByteBuffer buffer) {
        this.imageBuffer = buffer;
    }
}