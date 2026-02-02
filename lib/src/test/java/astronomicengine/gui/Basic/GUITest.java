package astronomicengine.gui.Basic;

import java.awt.Color;
import java.awt.Dimension;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import astronomicengine.shapes.GameObject;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.util.Builder.GameObjectBuilder.GraphicsScript;
import astronomicengine.window.AEWindow;

public class GUITest extends AEWindow {

    public GUITest() {
        super(new Dimension(800, 600), "GUI Test - Simple Shapes");
        setBounds(0, 800, 0, 600, -1, 1);

        GameObject button = new GameObjectBuilder()
                .setTranslate(new Vector3d(200, 100, 0))
                .addGraphics((g2d, rect) -> {
                    g2d.setColor(Color.gray);
                    g2d.fillRect(0, 0, 20, rect.height);
                }, new Dimension(100, 100))
                .build();

        objects.add(button);

    }

    @Override
    public void loop(double fps) {
        // TODO Auto-generated method stub
        super.loop(fps);

    }

    public static void main(String[] args) throws Exception {
        if (!GLFW.glfwInit())
            throw new Exception();
        new GUITest().startDisplaying();
    }
}
