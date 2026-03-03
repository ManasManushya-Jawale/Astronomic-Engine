package org.astroEngine.gui.Basic;

import java.awt.*;

import org.astroEngine.gui.GUIObject;
import org.lwjgl.glfw.GLFW;

import org.astroEngine.window.AEWindow;
import static org.lwjgl.opengl.GL11.*;

public class GUITest extends AEWindow {

    public GUITest() {
        super(new Dimension(800, 600), "GUI Test - Simple Shapes");

        GUIObject button = new GUIObject(new Rectangle(10, 10, 100, 100)){
            @Override
            public void paintComponent() {
                super.paintComponent();
                glBegin(GL_TRIANGLES);
                glColor3f(220 / 255f, 220 / 255f, 220 / 255f);
                glVertex2f(getBounds().x, getBounds().y);
                glVertex2f(getBounds().x + getBounds().width, getBounds().y);
                glVertex2f(
                        getBounds().x + getBounds().width / 2f,
                        getBounds().y + getBounds().height);
                glEnd();
            }
        };

        button.setPosition(100, 20);

        guiObjects.add(button);

    }

    @Override
    public void loop(double fps) {
        // TODO Auto-generated method stub
        super.loop(fps);

    }

    public static void main(String[] args) throws Exception {
        if (!GLFW.glfwInit())
            throw new Exception();
        new GUITest().initialStart();
    }
}
