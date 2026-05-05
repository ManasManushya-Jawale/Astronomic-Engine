package org.astroEngine.util;

import org.joml.*;
import org.joml.Math;

public class AEMath extends Math {
    public static class Rect3d {
        public Vector3d min; // (minX, minY, minZ)
        public Vector3d max; // (maxX, maxY, maxZ)

        public Rect3d(Vector3d p1, Vector3d p2) {
            // Normalize the points to ensure min is the bottom-left-back
            // and max is the top-right-front corner.
            this.min = new Vector3d(
                    Math.min(p1.x, p2.x),
                    Math.min(p1.y, p2.y),
                    Math.min(p1.z, p2.z)
            );
            this.max = new Vector3d(
                    Math.max(p1.x, p2.x),
                    Math.max(p1.y, p2.y),
                    Math.max(p1.z, p2.z)
            );
        }
    }

    public static boolean intersects(Rect3d cube1, Rect3d cube2) {
        // Check for overlap on all three axes
        boolean overlapX = (cube1.min.x <= cube2.max.x) && (cube1.max.x >= cube2.min.x);
        boolean overlapY = (cube1.min.y <= cube2.max.y) && (cube1.max.y >= cube2.min.y);
        boolean overlapZ = (cube1.min.z <= cube2.max.z) && (cube1.max.z >= cube2.min.z);

        // They intersect only if there is overlap on all axes
        return overlapX && overlapY && overlapZ;
    }

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

    public static float generateRandomFloat(float min, float max) {
        Random rand = new Random();
        // Generates a float in the range [min, max)
        return min + rand.nextFloat() * (max - min);
    }

    public static float[] addUV2DinVert(float[] vertex, float[] uv) {
        int vertexCount = vertex.length / 3;

        if (uv.length != vertexCount * 2) {
            throw new IllegalArgumentException("UV array size mismatch");
        }

        float[] result = new float[vertexCount * 5];

        int vIndex = 0;
        int uvIndex = 0;
        int rIndex = 0;

        for (int i = 0; i < vertexCount; i++) {
            // position (x, y, z)
            result[rIndex++] = vertex[vIndex++];
            result[rIndex++] = vertex[vIndex++];
            result[rIndex++] = vertex[vIndex++];

            // uv (u, v)
            result[rIndex++] = uv[uvIndex++];
            result[rIndex++] = uv[uvIndex++];
        }

        return result;
    }
}
