package org.astroEngine.graphics.geometry;

import org.astroEngine.graphics.shaders.VertexShader;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Cube extends VertexShader {
    float l, b, w;

    float[] bound;

    ArrayList<Float> result;

    public Cube(float l, float b, float w) {
        super(DEFAULT_SHADER_SPRITE);

        SHAPE_TYPE = GL11.GL_TRIANGLES;

        this.l = l;
        this.b = b;
        this.w = w;

        bound = new float[]{
                -l, -b, -w,
                -l, -b, w,
                -l, b, w,
                -l, b, -w,
                 l,-b, -w,
                 l,-b, w,
                 l,b, w,
                 l,b, -w
        };

        result = new ArrayList<>();

// FRONT (facing +Z)
        addQuad(1, 5, 6);
        addQuad(1, 6, 2);

// BACK (facing -Z)
        addQuad(0, 3, 7);
        addQuad(0, 7, 4);

// LEFT (facing -X)
        addQuad(0, 1, 2);
        addQuad(0, 2, 3);

// RIGHT (facing +X)
        addQuad(4, 7, 6);
        addQuad(4, 6, 5);

// BOTTOM (facing -Y)
        addQuad(0, 4, 5);
        addQuad(0, 5, 1);

// TOP (facing +Y)
        addQuad(3, 2, 6);
        addQuad(3, 6, 7);

        float[] resultArr = new float[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultArr[i] = result.get(i);
        }

        setVerticesArr(resultArr);

        depthBased = true;
    }

    private void addQuad(int a, int b, int c) {
        result.add(bound[a * 3]);
        result.add(bound[a * 3 + 1]);
        result.add(bound[a * 3 + 2]);

        result.add(bound[b * 3]);
        result.add(bound[b * 3 + 1]);
        result.add(bound[b * 3 + 2]);

        result.add(bound[c * 3]);
        result.add(bound[c * 3 + 1]);
        result.add(bound[c * 3 + 2]);

    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getL() {
        return l;
    }

    public void setL(float l) {
        this.l = l;
    }
}
