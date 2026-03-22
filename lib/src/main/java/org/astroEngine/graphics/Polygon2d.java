package org.astroEngine.graphics;

import org.joml.Matrix4d;
import org.joml.Vector2d;

import org.astroEngine.util.Astrodx;
import org.astroEngine.util.AEMath;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

public class Polygon2d extends Shape {

    Vector2d[] points;

    public Polygon2d(Color colors, Vector2d[] points) {
        super(colors);
        this.points = points;
        AEMath.centerVertices(points);
    }

    @Override
    public void draw(Matrix4d transform) {

        Astrodx.clearState();
        Astrodx.applyColor(this.color);
        Astrodx.applyTransforms(transform);
        glBegin(GL_POLYGON);
        for (Vector2d point : points) {
            glVertex2d(point.x, point.y);
        }
        glEnd();

        glPopMatrix();
    }

}
