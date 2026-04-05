package org.astroEngine.comp;

import org.astroEngine.shapes.GameObject;

public class Component {
    public static interface PreDrawable {
        public void beforeDraw();
    }

    public GameObject parent;

    public Component(GameObject parent) {
        this.parent = parent;
        
    }

    public Component() { }

    public void update(float delta) {}

    public void setParent(GameObject parent) {this.parent = parent;}
    public GameObject getParent() {return parent;}
    public void dispose() {}
    public void windowHide() {}
    public void windowShow() {}
    public void loopSetup() {}
}
