package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.std.DrawableComponent;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.shapes.Shape;

public class DrawableObject extends GameObject {
    private DrawableComponent draw;
    public DrawableObject(Shape shape) {
        super();
        draw = addDrawable(shape);
    }
}
