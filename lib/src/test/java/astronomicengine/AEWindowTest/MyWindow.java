package astronomicengine.AEWindowTest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import astronomicengine.comp.Component;
import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.shapes.GameObject;
import astronomicengine.graphics.Graphics2DSprite;
import astronomicengine.graphics.std.Polygon2d;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.window.AEWindow;

public class MyWindow extends AEWindow {

    public GameObject triangle;
    public GameObject button;
    public int speed = 5;

    public MyWindow() {
        super(new Dimension(800, 600), "My Custom Window");

        setBounds(0, 800, 0, 600, -1, 1);

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

        Graphics2DSprite g2s = new Graphics2DSprite(100, 100);
        Graphics2D g2d = g2s.getGraphics();
        g2d.fillRect(0, 0, 20, 100);
        g2s.uploadTexture();

        button = new GameObjectBuilder()
        .setTranslate(new Vector3d(200, 100, 0))
        .setRotation(new Vector3d(0, 0, 1))
        .addDrawable(g2s)
        .build();
        objects.add(button);
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        System.out.println(button.getTransformComponent().getTransform().toString());
    }
    public static void main(String[] arg) throws Exception {
        if (!GLFW.glfwInit())
            throw new Exception();

        MyWindow window = new MyWindow();
        window.startDisplaying();
    }
}
