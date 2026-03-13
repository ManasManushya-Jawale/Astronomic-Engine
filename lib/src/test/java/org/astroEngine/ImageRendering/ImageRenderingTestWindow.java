package org.astroEngine.ImageRendering;

import java.awt.Color;
import java.awt.Dimension;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.DrawableComponent;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.ImageSprite;
import org.astroEngine.graphics.Polygon2d;
import org.astroEngine.util.FileUtils;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.AEWindow;

public class ImageRenderingTestWindow extends AEWindow {
    public GameObject imageObject, triangle;

    public ImageRenderingTestWindow() {
        super(new Dimension(800, 600), "Image Rendering - Test Astronomic Engine");

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
