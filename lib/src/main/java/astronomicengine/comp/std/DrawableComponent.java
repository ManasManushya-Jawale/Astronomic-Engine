package astronomicengine.comp.std;

import astronomicengine.comp.Component;
import astronomicengine.shapes.GameObject;
import astronomicengine.shapes.Shape;

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
