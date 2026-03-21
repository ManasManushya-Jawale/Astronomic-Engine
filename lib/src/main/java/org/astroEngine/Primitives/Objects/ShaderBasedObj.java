package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.ShaderSprite;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

public class ShaderBasedObj extends DrawableObject {
    public ShaderBasedObj(ShaderSprite sprite) {
        super(sprite);
    }

    public void setVertices(ArrayList<Float> vertices) {
        ((ShaderSprite) getComponent(ShapeComp.class).getShape()).setVertices(vertices);
    }

    public void compile() {
        ((ShaderSprite) getComponent(ShapeComp.class).getShape()).compile();
    }
}