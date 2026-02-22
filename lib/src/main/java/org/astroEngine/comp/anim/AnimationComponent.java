package org.astroEngine.comp.anim;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.std.DrawableComponent;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.img.ImageSprite;
import org.astroEngine.util.annot.RequiresComponent;

@RequiresComponent(DrawableComponent.class)
public class AnimationComponent extends Component {
    private ArrayList<BufferedImage> keyframes;
    private float timer;
    private boolean start;

    private double elapsedTime = 0;
    private int keyframe = 0;

    public DrawableComponent draw;

    public AnimationComponent(ArrayList<BufferedImage> keyframes, float timer) {
        this.keyframes = keyframes;
        this.timer = timer;
        start = false;

        this.draw = null;
    }

    @Override
    public void setParent(GameObject parent) {
        // TODO Auto-generated method stub
        super.setParent(parent);
        this.draw = parent.getComponent(DrawableComponent.class);
    }

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub
        super.update(delta);

        if (!start) return;
        
        elapsedTime += delta;

        if (elapsedTime >= timer) {
            keyframe++;
            elapsedTime = 0;

            if (keyframe >= keyframes.size()) {
                keyframe = 0;
            }

            draw.setShape(new ImageSprite(keyframes.get(keyframe)));
        }
    }

    public void setKeyframe(int k) {
        keyframe = k;
    }

    public void stop() {
        start = false;
    }
    public void start() {
        start = true;
    }
}
