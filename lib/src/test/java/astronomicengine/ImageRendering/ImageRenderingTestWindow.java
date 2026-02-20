package astronomicengine.ImageRendering;

import java.awt.Color;
import java.awt.Dimension;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import astronomicengine.comp.Component;
import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.shapes.GameObject;
import astronomicengine.graphics.img.ImageSprite;
import astronomicengine.graphics.std.Polygon2d;
import astronomicengine.util.FileUtils;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.window.AEWindow;

public class ImageRenderingTestWindow extends AEWindow {
    public GameObject imageObject, triangle;

    public ImageRenderingTestWindow() {
        super(new Dimension(800, 600), "Image Rendering - Test Astronomic Engine");

        setBounds(0, 800, 0, 600, -1, 1);

        imageObject = new GameObjectBuilder()
                .setTranslate(new Vector3d(200, 100, 0))
                .setScale(new Vector3d(5))
                .addComponent(new DrawableComponent(
                        new ImageSprite(FileUtils.internal("/Man.png"), true)))
                .build();

        objects.add(imageObject);

        float speed = 5;

        triangle = new GameObjectBuilder()
                .setTranslate(new Vector3d(300, 200, 0))
                .addComponent(new DrawableComponent(new Polygon2d(
                        Color.RED,
                        new Vector2d[] {
                                new Vector2d(0, 0),
                                new Vector2d(-50, 100),
                                new Vector2d(50, 100),
                        })))
                .addComponent(new Component() {
                    @Override
                    public void update(float delta) {
                        // TODO Auto-generated method stub
                        super.update(delta);
                        float fps = 1 / delta;

                        if (keyPressed(GLFW.GLFW_KEY_W)) {
                            triangle.transform.translateRelative(new Vector3d(0, -speed, 0));
                        }
                        if (keyPressed(GLFW.GLFW_KEY_A)) {
                            triangle.getTransformComponent().rotateZ(-speed / fps);
                        }
                        if (keyPressed(GLFW.GLFW_KEY_S)) {
                            triangle.getTransformComponent().translateRelative(new Vector3d(0, speed, 0));
                        }
                        if (keyPressed(GLFW.GLFW_KEY_D)) {
                            triangle.getTransformComponent().rotateZ(speed / fps);
                        }
                    }
                })
                .build();

        objects.add(triangle);
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        imageObject.transform.rotateZ((float)(0.1f / fps));
    }

    public static void main(String[] args) {
        new ImageRenderingTestWindow().initialStart();
    }
}
