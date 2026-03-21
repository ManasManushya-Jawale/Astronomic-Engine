package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.ShapeComp;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Shape;

public class DrawableObject extends GameObject {
    private ShapeComp draw;
    public DrawableObject(Shape shape) {
        super();
        draw = addDrawable(shape);
    }

    public ShapeComp getDraw() {
        return draw;
    }

    public void setDraw(ShapeComp draw) {
        this.draw = draw;
    }

    public void setShape(Shape shape) {
        draw.setShape(shape);
    }

    public ShapeComp getShape() {
        return draw;
    }
}
