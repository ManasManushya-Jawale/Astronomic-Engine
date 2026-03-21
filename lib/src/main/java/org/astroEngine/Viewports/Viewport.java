package org.astroEngine.Viewports;

import org.astroEngine.Camera.Camera;

public class Viewport {
    private Camera camera;
    private int width, height;

    public Viewport(Camera camera) {
        this.camera = camera;
    }

    public void apply(long window, int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }
}
