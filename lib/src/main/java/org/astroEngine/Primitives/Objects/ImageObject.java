package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.std.DrawableComponent;
import org.astroEngine.graphics.img.ImageSprite;
import org.astroEngine.shapes.GameObject;

import java.io.File;

public class ImageObject extends GameObject {
    public ImageObject(File image) {
        super();
        addComponent(new DrawableComponent(new ImageSprite(image)));
    }
    public ImageObject(File image, boolean pixelPerfect) {
        super();
        addComponent(new DrawableComponent(new ImageSprite(image, pixelPerfect)));
    }
}
