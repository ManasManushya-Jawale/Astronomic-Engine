package org.astroEngine.util.Math;

import org.joml.Math;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class AEMath extends Math {
    public static void centerVertices(Vector3d[] vertices) {
        float sumX = 0, sumY = 0, sumZ = 0;

        // Sum all coordinates
        for (Vector3d v : vertices) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }

        // Compute centroid
        float cx = sumX / vertices.length;
        float cy = sumY / vertices.length;
        float cz = sumZ / vertices.length;

        // Shift all vertices so centroid is at (0,0,0)
        for (Vector3d v : vertices) {
            v.x -= cx;
            v.y -= cy;
            v.z -= cz;
        }
    }

    public static void centerVertices(Vector2d[] vertices) {
        double sumX = 0, sumY = 0;

        // Sum all coordinates
        for (Vector2d v : vertices) {
            sumX += v.x;
            sumY += v.y;
        }

        // Compute centroid
        double cx = sumX / vertices.length;
        double cy = sumY / vertices.length;

        // Shift all vertices so centroid is at (0,0,0)
        for (Vector2d v : vertices) {
            v.x -= cx;
            v.y -= cy;
        }
    }

    public static Matrix4d buildCenteredTransform(
            double x, double y,
            double width, double height,
            double angleRadians,
            double scaleX, double scaleY) {

        return new Matrix4d()
                // move to desired screen position
                .translate(x, y, 0)
                // rotate around center
                .rotateZ(angleRadians)
                // scale around center
                .scale(scaleX, scaleY, 1.0);
    }
}
