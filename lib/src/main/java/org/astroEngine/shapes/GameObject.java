package org.astroEngine.shapes;

import java.util.ArrayList;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.comp.Transform;
import org.astroEngine.graphics.Shape;
import org.astroEngine.AEWindow;
import org.joml.Matrix4d;

public class GameObject {
    ArrayList<Component> components;
    public Transform transform;
    private ArrayList<GameObject> children;
    private AEWindow parent;
    private int layer = 1;

    public ArrayList<GameObject> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<GameObject> children) {
        this.children = children;
    }

    public GameObject() {
        this.components = new ArrayList<>();
        children = new ArrayList<>();
        addTransform();
    }
    @Override
    public GameObject clone() {
        try {
            GameObject copy = (GameObject) super.clone();

            copy.transform = this.getTransformComponent();
            copy.components = this.getComponents();
            copy.layer = this.layer;
            copy.parent = this.parent;

            return copy;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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

    public Matrix4d getTransform() {
        return getTransformComponent().transform;
    }

    public int getLayer() {
        return layer;
    }

    public int getLayerOrder() {
        return -layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
