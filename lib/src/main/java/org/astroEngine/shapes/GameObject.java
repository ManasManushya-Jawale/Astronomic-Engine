package org.astroEngine.shapes;

import java.util.ArrayList;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.comp.Transform;
import org.astroEngine.graphics.Shape;
import org.astroEngine.AEWindow;

public class GameObject {
    ArrayList<Component> components;
    public Transform transform;
    private AEWindow parent;

    public GameObject() {
        this.components = new ArrayList<>();
        addTransform();
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

     @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return (T) c;
            }
        }
        return null;
    }

    public Transform getTransformComponent() {
        transform = getComponent(Transform.class);
        return transform;
    }

    public void setTransformComponent(Transform component) {
        for (Component c : components) {
            if (c.getClass() == Transform.class) {
                ((Transform) c).setTransform(component.getTransform());
            }
        }
    }

    public Transform addTransform() {
        transform = new Transform();
        return ((Transform)addComponent(transform));
    }

    public Component addComponent(Component comp) {
        comp.setParent(this);
        components.add(comp);
        return comp;
    }

    public void initComponentList() {
        this.components = new ArrayList<>();
    }

    public AEWindow getParent() {
        return parent;
    }
    
    public ShapeComp addDrawable(Shape shape) {
        return ((ShapeComp) addComponent(new ShapeComp(shape)));
    }

    public void setParent(AEWindow parent) {
        this.parent = parent;
    }
    
}
