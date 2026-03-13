package org.astroEngine.shapes;

import java.util.ArrayList;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.DrawableComponent;
import org.astroEngine.comp.TransformComponent;
import org.astroEngine.graphics.Shape;
import org.astroEngine.AEWindow;

public class GameObject {
    ArrayList<Component> components;
    public TransformComponent transform;
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

    public TransformComponent getTransformComponent() {
        transform = getComponent(TransformComponent.class);
        return transform;
    }

    public void setTransformComponent(TransformComponent component) {
        for (Component c : components) {
            if (c.getClass() == TransformComponent.class) {
                ((TransformComponent) c).setTransform(component.getTransform());
            }
        }
    }

    public TransformComponent addTransform() {
        transform = new TransformComponent();
        return ((TransformComponent)addComponent(transform));
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
    
    public DrawableComponent addDrawable(Shape shape) {
        return ((DrawableComponent) addComponent(new DrawableComponent(shape)));
    }

    public void setParent(AEWindow parent) {
        this.parent = parent;
    }
}
