package org.astroEngine.Primitives.Objects;

import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.ImageSprite;
import org.astroEngine.shapes.GameObject;

import java.io.File;

public class ImageObject extends GameObject {
    public ImageObject(File image) {
        super();
        addComponent(new ShapeComp(new ImageSprite(image)));
    }
    public ImageObject(File image, boolean pixelPerfect) {
        super();
        addComponent(new ShapeComp(new ImageSprite(image, pixelPerfect)));
    }
}
