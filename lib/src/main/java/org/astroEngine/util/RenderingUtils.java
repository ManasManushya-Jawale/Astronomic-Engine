package org.astroEngine.util;

import org.astroEngine.shapes.Shape;
import org.joml.Math;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

public class RenderingUtils {
    public static void drawShape(Shape shape, Vector3d pos) {
        shape.draw(new Matrix4d()
                .translate(pos.x, pos.y, pos.z));
    }

    public static void drawCircle(float radius, int angle, Color color) {
        glBegin(GL_POLYGON);
        float theta = 0;

        for (int i = 0; i < angle; i++) {
            float sin = Math.sin(Math.toRadians(theta));
            float cos = Math.cos(Math.toRadians(theta));

            glVertex2f(sin*radius, cos*radius);
        }

        glEnd();
    }
}
