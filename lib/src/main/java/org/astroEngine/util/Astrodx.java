package org.astroEngine.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.graphics.ImageSprite;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;

public class Astrodx {
    public static void applyTransforms(Matrix4d mvp) {
        DoubleBuffer db = BufferUtils.createDoubleBuffer(16);
        mvp.get(db);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();          // projection stays identity

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrixd(db);         // load FULL MVP here


    }

    // Clear both transformation and color state
    public static void clearState() {
        // Reset transformation
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity(); // reset to identity matrix

        // Reset color to white (fully opaque)
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

    }

    // Apply a simple RGB color
    public static void applyColor(Color color) {
        glColor4f(color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                color.getAlpha() / 255f);
    }

    // Convert BufferedImage → OpenGL texture
    public static int loadTexture(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // G
                buffer.put((byte) (pixel & 0xFF)); // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }
        buffer.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Filtering & wrapping
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }

    public static ArrayList<BufferedImage> packTexture(File rejistry) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(rejistry));
            String line;

            ArrayList<String> paths = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                paths.add(line);
            }

            ArrayList<BufferedImage> images = new ArrayList<>();

            paths.forEach(path -> {
                try {
                    images.add(ImageIO.read(new File(path)));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            return images;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<BufferedImage> packTextureFromResource(File rejistry) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(rejistry));
            String line;

            ArrayList<String> paths = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                paths.add(line);
            }

            ArrayList<BufferedImage> images = new ArrayList<>();

            paths.forEach(path -> {
                try {
                    images.add(ImageIO.read(Files.internal(path)));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ;
            });
            return images;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public boolean collides(ImageSprite obj1, ImageSprite obj2) {
        return obj1.getBounds().intersects(obj2.getBounds());
    }

}
