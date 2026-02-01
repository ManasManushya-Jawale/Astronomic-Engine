package astronomicengine.graphics.std;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2d;

import astronomicengine.shapes.Shape;
import astronomicengine.util.GameUtils;
import astronomicengine.util.Math.AEMath;

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

        glBegin(GL_POLYGON);
        for (int i = 0; i < points.length; i++) {
            glVertex2d(points[i].x, points[i].y);
        }
        glEnd();
    }

}
