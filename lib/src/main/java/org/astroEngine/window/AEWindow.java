package org.astroEngine.window;

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
import java.util.Objects;

import org.astroEngine.gui.GUIObject;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.astroEngine.util.GameUtils;
import org.joml.Matrix4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.std.DrawableComponent;
import org.astroEngine.comp.std.TransformComponent;
import org.astroEngine.shapes.GameObject;

/**
 * AEWindow is a window
 */
public class AEWindow {
    public long window;
    public Dimension size;
    public String title;

    public Color background = Color.GREEN;

    public ArrayList<GameObject> objects;
    public ArrayList<GUIObject> guiObjects;

    public boolean runInBackground = false;
    private boolean visible = true;

    private Matrix4d projectionMatrix;

    public AEWindow(Dimension size, String title) {
        this.size = size;
        this.title = title;

        this.objects = new ArrayList<>();
        this.guiObjects = new ArrayList<>();

        if (!GLFW.glfwInit())
            return;

        preWindowInitialization();
        window = GLFW.glfwCreateWindow(size.width, size.height, title, 0, 0);

        GLFW.glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            glViewport(0, 0, width, height);
        });

        projectionMatrix = new Matrix4d();

    }

    public Matrix4d getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4d projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setBackground(Color color) {
        this.background = color;
    }

    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(
                background.getRed() / 255f,
                background.getGreen() / 255f,
                background.getBlue() / 255f,
                background.getAlpha() / 255f);

        for (GameObject object : objects) {
            TransformComponent transform;
            if ((transform = object.getComponent(TransformComponent.class)) != null) {
                DrawableComponent draw;
                if ((draw = object.getComponent(DrawableComponent.class)) != null) {
                    Matrix4d mvp = new Matrix4d(projectionMatrix)
                            .mul(transform.getTransform());
                    draw.getShape().draw(mvp);
                }
            }
        }

        for (GUIObject object : guiObjects) {
            object.paintComponent();
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

    public void initialStart() {
        if (runInBackground^visible) {
            startDisplaying();
        }
    }

    public void preWindowInitialization() { }
    public void loopSetup() { }

    private void startDisplaying() {
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        loopSetup();

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

    public void hideWindow() {
        GLFW.glfwHideWindow(window);
        visible = false;
    }

    public void showWindow() {
        GLFW.glfwShowWindow(window);
        visible = true;
    }

    public void destroyWindow() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwSetErrorCallback(null).free();
    }

    public boolean keyPressed(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
    public boolean keyRelease(int key) { return GLFW.glfwGetKey(window, key) == GLFW.GLFW_RELEASE; }
    public boolean keyRepeat(int key) { return GLFW.glfwGetKey(window, key) == GLFW.GLFW_REPEAT; }

    public void addObject(GameObject object) {
        object.setParent(this);
        objects.add(object);
    }

    public void removeObject(GameObject object) {
        object.setParent(null);
        objects.remove(object);
    }

    public void removeGUIObject(GUIObject guiObject) {
        guiObjects.remove(guiObject);
    }

    public void addGUIObject(GUIObject guiObject) {
        guiObjects.add(guiObject);
    }

    public void applyTransformations(Matrix4d model) {
        GameUtils.applyTransforms(new Matrix4d(projectionMatrix).mul(model));
    }
}
