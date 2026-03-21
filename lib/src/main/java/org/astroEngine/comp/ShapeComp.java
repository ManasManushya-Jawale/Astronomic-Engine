package org.astroEngine.comp;

import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Shape;

public class ShapeComp extends Component {
    Shape shape;
    
    public ShapeComp(GameObject parent, Shape shape) {
        this.shape = shape;
    }

    public ShapeComp(Shape shape) {
        this.shape = shape;
    }

    public void setShape(Shape shape) {this.shape = shape;}
    
    public Shape getShape() {return shape;}
}
