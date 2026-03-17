package org.astroEngine.Viewports;

import org.astroEngine.Camera.Camera;

public abstract class Viewport {
    private Camera camera;

    public Viewport(Camera camera) {
        this.camera = camera;
    }

    public abstract void apply(long window, int w, int h);
}
