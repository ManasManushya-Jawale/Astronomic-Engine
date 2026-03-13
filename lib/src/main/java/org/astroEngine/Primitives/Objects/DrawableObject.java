package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.DrawableComponent;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Shape;

public class DrawableObject extends GameObject {
    private DrawableComponent draw;
    public DrawableObject(Shape shape) {
        super();
        draw = addDrawable(shape);
    }
}
