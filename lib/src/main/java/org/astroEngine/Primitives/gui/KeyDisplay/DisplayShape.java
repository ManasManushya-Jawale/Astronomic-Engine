package org.astroEngine.Primitives.gui.KeyDisplay;

import org.astroEngine.graphics.ShaderSprite;
import org.astroEngine.graphics.Shape;
import org.joml.Matrix4d;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

public class DisplayShape extends ShaderSprite {
    String text;

    public DisplayShape(String text) {
        super(DEFAULT_SHADER);

        this.text = text;
    }

    void drawText(String text, float x, float y, float scale)
    {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96);

            FloatBuffer vertices = stack.mallocFloat(text.length() * 6 * 4);

            float xpos = x;

            for(char c : text.toCharArray()) {

                STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);

                stbtt_GetBakedQuad(
                        charData,
                        512,
                        512,
                        c - 32,
                        stack.floats(xpos),
                        stack.floats(y),
                        q,
                        true
                );

                float x0 = q.x0();
                float y0 = q.y0();
                float x1 = q.x1();
                float y1 = q.y1();

                float s0 = q.s0();
                float t0 = q.t0();
                float s1 = q.s1();
                float t1 = q.t1();

                // triangle 1
                vertices.put(x0).put(y0).put(s0).put(t0);
                vertices.put(x1).put(y0).put(s1).put(t0);
                vertices.put(x1).put(y1).put(s1).put(t1);

                // triangle 2
                vertices.put(x0).put(y0).put(s0).put(t0);
                vertices.put(x1).put(y1).put(s1).put(t1);
                vertices.put(x0).put(y1).put(s0).put(t1);

                xpos = q.x1();
            }

            vertices.flip();
        }
    }
}
