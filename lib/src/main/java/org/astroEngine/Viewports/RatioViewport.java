package org.astroEngine.Viewports;

import org.astroEngine.Camera.Camera;
import org.astroEngine.Camera.PerspectiveCamera;
import org.lwjgl.opengl.GL11;

public class RatioViewport extends Viewport {
    public RatioViewport(Camera camera, float ratio) {
        super(camera);
        this.targetAspect = ratio;
    }

    private float targetAspect = 16f / 9f;

    @Override
    public void apply(long window, int w, int h) {
        float windowAspect = (float) w / h;

        int vpX = 0, vpY = 0;
        int vpW = w, vpH = h;

        if (windowAspect > targetAspect) {
            // too wide → pillarbox
            vpW = (int) (h * targetAspect);
            vpX = (w - vpW) / 2;
        } else {
            // too tall → letterbox
            vpH = (int) (w / targetAspect);
            vpY = (h - vpH) / 2;
        }

        GL11.glViewport(vpX, vpY, vpW, vpH);

        if (camera instanceof PerspectiveCamera) {
            ((PerspectiveCamera) camera).setAspect(((float) (w / h)));
        }
    }

    public float getTargetAspect() {
        return targetAspect;
    }

    public void setTargetAspect(float targetAspect) {
        this.targetAspect = targetAspect;
    }
}