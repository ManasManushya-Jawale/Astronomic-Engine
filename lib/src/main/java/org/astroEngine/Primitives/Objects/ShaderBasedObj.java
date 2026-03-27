package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.shaders.VertexShader;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

public class ShaderBasedObj extends DrawableObject {
    public ShaderBasedObj(VertexShader sprite) {
        super(sprite);
    }

    public void setVertices(ArrayList<Float> vertices) {
        ((VertexShader) getComponent(ShapeComp.class).getShape()).setVertices(vertices);
    }

    public void compile() {
        ((VertexShader) getComponent(ShapeComp.class).getShape()).compile();
    }
}