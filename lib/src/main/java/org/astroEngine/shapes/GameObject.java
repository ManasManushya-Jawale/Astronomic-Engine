package org.astroEngine.shapes;

import java.util.ArrayList;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.std.TransformComponent;

public class GameObject {
    ArrayList<Component> components;
    public TransformComponent transform;

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
}
