package org.astroEngine.Viewports;

import org.astroEngine.Camera.Camera;
import org.lwjgl.opengl.GL11;

public class ScaleViewport extends Viewport {
    public ScaleViewport(Camera camera) {
        super(camera);
    }

    @Override
    public void apply(long window, int w, int h) {
        super.apply(window, w, h);
        GL11.glViewport(0, 0, w, h);
    }
}
