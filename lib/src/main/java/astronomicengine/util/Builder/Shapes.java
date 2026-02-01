package astronomicengine.util.Builder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.joml.Math;
import org.joml.Matrix3d;
import org.joml.Vector2d;

import astronomicengine.graphics.std.Polygon2d;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

public class Shapes {
    public static Polygon2d equilateralTriangle(float size, Color color) {

        Matrix3d points = new Matrix3d();

        points.m00 = 0;
        points.m01 = 0;
        points.m02 = 0;

        points.m10 = size;
        points.m11 = 0;
        points.m12 = 0;

        points.m20 = size / 2;
        points.m21 = size * Math.sin(Math.toRadians(60));
        points.m22 = 0;

        return new Polygon2d(
                color,
                new Vector2d[] {
                        new Vector2d(0, 0),
                        new Vector2d(size, 0),
                        new Vector2d(size / 2, size * Math.sin(Math.toRadians(60)))
                });
    }

    public static ByteBuffer convertToByteBuffer(BufferedImage bimage) {
        int width = bimage.getWidth();
        int height = bimage.getHeight();
        int[] pixels = bimage.getRGB(0, 0, width, height, null, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        // Pack pixels as RGBA
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                buffer.put((byte) (pixel & 0xFF));         // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }

        buffer.flip();
        return buffer;
    }

}
