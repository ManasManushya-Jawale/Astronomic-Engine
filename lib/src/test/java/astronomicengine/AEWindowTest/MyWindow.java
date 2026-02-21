package astronomicengine.AEWindowTest;

import java.awt.*;

import astronomicengine.Primitives.Objects.ImageObject;
import astronomicengine.util.FileUtils;
import astronomicengine.util.RenderingUtils;
import com.google.common.io.FileBackedOutputStream;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import astronomicengine.comp.Component;
import astronomicengine.shapes.GameObject;
import astronomicengine.graphics.Graphics2DSprite;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.window.AEWindow;

public class MyWindow extends AEWindow {

    public GameObject triangle;
    public GameObject button;
    public float speed = 3/2f;
    public float steer = 1;

    public MyWindow() {
        super(new Dimension(800, 600), "My Custom Window");

        setBounds(0, 800, 0, 600, -1, 1);
        triangle = new GameObjectBuilder(new ImageObject(FileUtils.internal("/cars/HayaBabu.png"), true))
                .setTranslate(new Vector3d(400, 300, 0))
                .setScale(new Vector3d(6))
                .build();

        objects.add(triangle);

        Graphics2DSprite.GraphicsScript script = (g2d, w, h) -> {
            g2d.setColor(Color.BLUE);
            g2d.fillRoundRect(0, 0, w, h, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("JetBrains Mono NF", Font.PLAIN, 24));
            g2d.drawString("Helo", 20, 30);
            return g2d;
        };

        Graphics2DSprite g2s = new Graphics2DSprite(100, 100, script);

        button = new GameObjectBuilder()
                .setTranslate(new Vector3d(100, 100, 0))
                .setRotation(new Vector3d(0, 0, 1))
                .addDrawable(g2s)
                .build();
        objects.add(button);
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        Vector3d oldPos = triangle.getTransformComponent().getPosition();
        Vector3d oldRot = triangle.getTransformComponent().getRotation();

        if (keyPressed(GLFW.GLFW_KEY_W)) {
            triangle.transform.translateRelative(new Vector3d(0, -speed, 0));
        }
        if (keyPressed(GLFW.GLFW_KEY_A)) {
            triangle.getTransformComponent().rotateZ((float) (-steer / fps));
        }
        if (keyPressed(GLFW.GLFW_KEY_S)) {
            triangle.getTransformComponent().translateRelative(new Vector3d(0, speed, 0));
        }
        if (keyPressed(GLFW.GLFW_KEY_D)) {
            triangle.getTransformComponent().rotateZ((float) (steer / fps));
        }

        Vector3d newPos =triangle.getTransformComponent().getPosition();
        Vector3d newRot =triangle.getTransformComponent().getRotation();

        Vector3d d = oldPos.sub(newPos);
        getProjectionMatrix().translate(d);

        Vector3d dR = oldRot.sub(newRot);

        getProjectionMatrix().rotateAround(new Quaterniond().rotateZ(dR.z), newPos.x, newPos.y, newPos.z);
    }

    public static void main(String[] arg) throws Exception {
        if (!GLFW.glfwInit())
            throw new Exception();

        MyWindow window = new MyWindow();
        window.initialStart();
    }
}
