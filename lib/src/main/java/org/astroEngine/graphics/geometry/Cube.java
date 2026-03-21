package org.astroEngine.graphics.geometry;

import org.astroEngine.graphics.ShaderSprite;
import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Cube extends ShaderSprite {
    float l, b, w;

    float[] bound = new float[]{
            -1, -1, -1,
            -1, -1, 1,
            -1, 1, 1,
            -1, 1, -1,
            1, -1, -1,
            1, -1, 1,
            1, 1, 1,
            1, 1, -1
    };

    ArrayList<Float> result;

    public Cube(float l, float b, float w) {
        super(DEFAULT_SHADER_SPRITE);

        SHAPE_TYPE = GL11.GL_QUADS;

        this.l = l;
        this.b = b;
        this.w = w;

        result = new ArrayList<>();

        addQuad(0,1,2,3); // left
        addQuad(4,5,6,7); // right
        addQuad(0,1,5,4); // bottom
        addQuad(2,3,7,6); // top
        addQuad(1,2,6,5); // front
        addQuad(0,3,7,4); // back

        float[] resultArr = new float[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultArr[i] = result.get(i);
        }

        setVerticesArr(resultArr);
    }

    private void addQuad(int a, int b, int c, int d) {
        result.add(bound[a * 3]);
        result.add(bound[a * 3 + 1]);
        result.add(bound[a * 3 + 2]);

        result.add(bound[b * 3]);
        result.add(bound[b * 3 + 1]);
        result.add(bound[b * 3 + 2]);

        result.add(bound[c * 3]);
        result.add(bound[c * 3 + 1]);
        result.add(bound[c * 3 + 2]);

        result.add(bound[d * 3]);
        result.add(bound[d * 3 + 1]);
        result.add(bound[d * 3 + 2]);
    }

}
