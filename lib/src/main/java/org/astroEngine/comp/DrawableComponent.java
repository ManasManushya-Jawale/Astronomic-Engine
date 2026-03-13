package org.astroEngine.comp;

import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Shape;

public class DrawableComponent extends Component {
    Shape shape;
    
    public DrawableComponent(GameObject parent, Shape shape) {
        this.shape = shape;
    }

    public DrawableComponent(Shape shape) {
        this.shape = shape;
    }

    public void setShape(Shape shape) {this.shape = shape;}
    
    public Shape getShape() {return shape;}
}
