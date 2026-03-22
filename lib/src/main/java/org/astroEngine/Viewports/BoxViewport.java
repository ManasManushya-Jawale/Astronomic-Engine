package org.astroEngine.Viewports;

import org.astroEngine.Camera.Camera;
import org.astroEngine.Camera.OrthographicCamera;
import org.lwjgl.opengl.GL11;

public class BoxViewport extends Viewport {
    public BoxViewport(Camera camera) {
        super(camera);
    }

    @Override
    public void apply(long window, int w, int h) {
        super.apply(window, w, h);

        super.apply(window, w, h);

        int min = Math.min(w, h);
        int size = Math.min(w, h);
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        GL11.glViewport(x, y, min, min);
        if (camera instanceof OrthographicCamera) {
            ((OrthographicCamera) camera).setOrtho(0, min, min, 0, -1, 1);
        }
    }
}
