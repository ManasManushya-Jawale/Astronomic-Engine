package astronomicengine.ImageRendering;

import java.awt.Dimension;

import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.shapes.GameObject;
import astronomicengine.graphics.img.ImageSprite;
import astronomicengine.util.FileUtils;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.window.AEWindow;

public class ImageRenderingTestWindow extends AEWindow {
    public GameObject imageObject;

    public ImageRenderingTestWindow() {
        super(new Dimension(800, 600), "Image Rendering - Test Astronomic Engine");

        setBounds(0, 800, 0, 600, -1, 1);

        imageObject = new GameObjectBuilder()
                .setTranslate(new Vector3d(200, 100, 0))
                .setScale(new Vector3d(2))
                .centerTransform(64, 128)
                .addComponent(new DrawableComponent(
                        new ImageSprite(FileUtils.internal("/Man.png"), true)))
                .build();

        objects.add(imageObject);

    }

    @Override
    public void loop(double fps) {
        imageObject.getTransformComponent().rotateZ(((float)(1 / fps)));

        Vector3d pos = new Vector3d();
        imageObject.getTransformComponent().getPosition();

        System.out.println(pos.toString());
    }

    public static void main(String[] args) {
        new ImageRenderingTestWindow().startDisplaying();
    }
}
