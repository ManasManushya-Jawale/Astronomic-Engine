package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.ComponentBuilder;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.Shape;
import org.astroEngine.graphics.shaders.VertexShader;

public class ShadedObject extends DrawableObject {

    public ShadedObject(VertexShader shape) {
        super(shape);

        addComponent(new ComponentBuilder().update(((comp, delta) -> {

        })).build());
    }
}
