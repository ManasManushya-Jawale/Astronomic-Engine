package astronomicengine.Primitives.Objects;

import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.graphics.img.ImageSprite;
import astronomicengine.shapes.GameObject;

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
