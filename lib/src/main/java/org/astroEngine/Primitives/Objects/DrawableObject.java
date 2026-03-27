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

    @Override
    public DrawableObject clone() {
        DrawableObject copy = new DrawableObject(this.getShape().getShape());

        copy.transform = this.getTransformComponent();
        copy.setTransformComponent(transform);
        copy.setParent(copy.getParent());
        copy.getComponents().addAll(this.getComponents());

        return copy;

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
