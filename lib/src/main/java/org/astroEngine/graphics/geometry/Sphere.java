package org.astroEngine.graphics.geometry;

import org.astroEngine.graphics.shaders.VertexShader;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Sphere extends VertexShader {
    public Sphere(int r, int xCuts, int yCuts) {
        super(DEFAULT_SHADER, generateSphere(r, xCuts, yCuts), GL11.GL_TRIANGLES);
        depthBased = true;
    }

    private static ArrayList<Float> generateSphere(float radius, int stacks, int sectors) {
        ArrayList<Float> verts = new ArrayList<>();

        for (int i = 0; i < stacks; i++) {

            double stackAngle1 = Math.PI / 2 - i * Math.PI / stacks;
            double stackAngle2 = Math.PI / 2 - (i + 1) * Math.PI / stacks;

            double xy1 = radius * Math.cos(stackAngle1);
            double y1 = radius * Math.sin(stackAngle1);

            double xy2 = radius * Math.cos(stackAngle2);
            double y2 = radius * Math.sin(stackAngle2);

            for (int j = 0; j < sectors; j++) {

                double sector1 = j * 2 * Math.PI / sectors;
                double sector2 = (j + 1) * 2 * Math.PI / sectors;

                float x1 = (float)(xy1 * Math.cos(sector1));
                float z1 = (float)(xy1 * Math.sin(sector1));

                float x2 = (float)(xy2 * Math.cos(sector1));
                float z2 = (float)(xy2 * Math.sin(sector1));

                float x3 = (float)(xy2 * Math.cos(sector2));
                float z3 = (float)(xy2 * Math.sin(sector2));

                float x4 = (float)(xy1 * Math.cos(sector2));
                float z4 = (float)(xy1 * Math.sin(sector2));

                // triangle 1
                verts.add(x1); verts.add((float)y1); verts.add(z1);
                verts.add(x2); verts.add((float)y2); verts.add(z2);
                verts.add(x3); verts.add((float)y2); verts.add(z3);

                // triangle 2
                verts.add(x1); verts.add((float)y1); verts.add(z1);
                verts.add(x3); verts.add((float)y2); verts.add(z3);
                verts.add(x4); verts.add((float)y1); verts.add(z4);
            }
        }

        return verts;
    }
}
