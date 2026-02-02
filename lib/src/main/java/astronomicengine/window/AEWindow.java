package astronomicengine.window;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import astronomicengine.comp.Component;
import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.comp.std.TransformComponent;
import astronomicengine.shapes.GameObject;
import astronomicengine.util.GameUtils;

public class AEWindow {
    public long window;
    public Dimension size;
    public String title;

    public Color background = Color.GREEN;

    /**
     * Near - (x, y, z) -> (left, top, near)
     * Far - (x, y, z) -> (right, bottom, far)
     **/
    public Vector3D near, far;

    public ArrayList<GameObject> objects, uiObjects;

    public AEWindow(Dimension size, String title) {
        this.size = size;
        this.title = title;

        this.objects = new ArrayList<>();
        this.uiObjects = new ArrayList<>();


        if (!GLFW.glfwInit())
            return;
        window = GLFW.glfwCreateWindow(size.width, size.height, title, 0, 0);

        GLFW.glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            setBounds(0, width, 0, width, near.getZ(), far.getZ());
            glViewport(0, 0, width, height);
        });

    }

    public void setBounds(double l, double r, double t, double b, double n, double f) {
        near = new Vector3D(l, t, n);
        far = new Vector3D(r, b, f);

    }

    public void setBounds(Vector3D near, Vector3D far) {
        this.near = near;
        this.far = far;

    }

    public void setBackground(Color color) {
        this.background = color;
    }

    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(
                background.getRed() / 255f,
                background.getGreen() / 255f,
                background.getBlue() / 255f,
                background.getAlpha() / 255f);

        for (GameObject object : objects) {
            TransformComponent transform = object.getComponent(TransformComponent.class);

            DrawableComponent draw;
            if ((draw = object.getComponent(DrawableComponent.class)) != null) {
                draw.getShape().draw(transform.getTransform());
            }

        }

        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void loop(double fps) {
        for (GameObject objects : objects) {
            for (Component components : objects.getComponents()) {
                components.update((float) (1 / fps));
            }
        }
    }

    public void startDisplaying() {

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(near.getX(), far.getX(), far.getY(), near.getY(), near.getZ(), far.getZ());
        glMatrixMode(GL_MODELVIEW);

        long lastFrameTime = System.nanoTime();
        double fps;

        while (!GLFW.glfwWindowShouldClose(window)) {
            draw();

            long now = System.nanoTime();
            double deltaSeconds = (now - lastFrameTime) / 1_000_000_000.0;
            fps = 1.0 / deltaSeconds;
            lastFrameTime = now;

            loop(fps);
        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public boolean keyPressed(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}
