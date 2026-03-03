package org.astroEngine.comp;

import org.astroEngine.shapes.GameObject;

public class Component {
    public GameObject parent;

    public Component(GameObject parent) {
        this.parent = parent;
        
    }

    public Component() { }

    public void update(float delta) {}

    public void setParent(GameObject parent) {this.parent = parent;}
    public GameObject getParent() {return parent;}
}
