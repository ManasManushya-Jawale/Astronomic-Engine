package org.astroEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.util.*;

import org.astroEngine.Constants.vSyncState;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.util.Astrodx;
import org.joml.Matrix4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.comp.Transform;
import org.astroEngine.shapes.GameObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * AEWindow is a window that does alot of things.
 * This window is used to display objects onto screen and is ECS based.
 * <ul>
 *      <li>It has Entity Component System built-in</li>
 *     <li>It has a Projection Matrix to be used by camera</li>
 * </ul>
 */
public class AEWindow {
    public long window;
    public Dimension size;
    public String title;

    public Color background = Color.GREEN;

    public ArrayList<GameObject> objects;

    private Queue<GameObject> addQueue = new LinkedList<>(),
            removeQueue = new LinkedList<>();

    public boolean runInBackground = false;
    private boolean visible = true;

    private Matrix4d projectionMatrix;
    private boolean depthTestEnabled = false;

    private vSyncState vSync = vSyncState.ENABLE_IF_SUPPORTS;

    public vSyncState getVSync() {
        return vSync;
    }

    public void setVSync(vSyncState vSync) {
        this.vSync = vSync;

        GLFW.glfwSwapInterval(vSync.getValue());
    }


    public AEWindow(Dimension size, String title) {
        this.size = size;
        this.title = title;

        this.objects = new ArrayList<>();
        addQueue = new LinkedList<>();
        removeQueue = new LinkedList<>();

        if (!GLFW.glfwInit())
            return;

        preWindowInitialization();
        window = GLFW.glfwCreateWindow(size.width, size.height, title, 0, 0);

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
            Transform transform;
            if ((transform = object.getComponent(Transform.class)) != null) {
                ShapeComp draw;
                if ((draw = object.getComponent(ShapeComp.class)) != null) {
                    Matrix4d mvp = new Matrix4d(projectionMatrix)
                            .mul(transform.getTransform());
                    draw.getShape().draw(mvp);
                }
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

    public void initialStart() {
        if (runInBackground^visible) {
            startDisplaying();
        }
    }

    /**
     * This runs before the window is created
     */
    public void preWindowInitialization() { }

    /***
     This runs after the window is created and GL capabilities are created
     */
    public void loopSetup() {
        objects.addAll(addQueue);
        addQueue.clear();
    }

    private void startDisplaying() {
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        loopSetup();
        GLFW.glfwSwapInterval(vSync.getValue());

        long lastFrameTime = System.nanoTime();
        double fps;

        while (!GLFW.glfwWindowShouldClose(window)) {
            beforeDraw();

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

    public void beforeDraw() {
        objects.addAll(addQueue);
        objects.removeAll(removeQueue);

        if (!addQueue.isEmpty()) {
            objects.sort(Comparator
                    .comparingDouble(GameObject::getLayerOrder)
            );
        }

        addQueue.clear();
        removeQueue.clear();

    }

    public void hideWindow() {
        GLFW.glfwHideWindow(window);
        visible = false;

        for (GameObject objects : objects) {
            for (Component components : objects.getComponents()) {
                components.windowHide();
            }
        }
    }

    public void showWindow() {
        GLFW.glfwShowWindow(window);
        visible = true;

        for (GameObject objects : objects) {
            for (Component components : objects.getComponents()) {
                components.windowShow();
            }
        }
    }

    /**
     * <b>Note: </b>this does not destroy the object but instead
     * only removes the window from openGL's system
     */
    public void destroyWindow() {
        dispose();
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
        addQueue.add(object);
    }

    public void removeObject(GameObject object) {
        removeQueue.add(object);
    }

    public void applyTransformations(Matrix4d model) {
        Astrodx.applyTransforms(new Matrix4d(projectionMatrix).mul(model));
    }

     public void setViewport(Viewport viewport) {
        GLFW.glfwSetWindowSizeCallback(window, (win, w ,h) -> {
            viewport.apply(window, w, h);
        });
    }

    public void setResizeCallback(GLFWWindowSizeCallback callback) {
        GLFW.glfwSetWindowSizeCallback(window, callback);
    }

    public void setMouseMoveCallback(GLFWMouseButtonCallback callback) {
        GLFW.glfwSetMouseButtonCallback(window, callback);
    }

    public void pushBack(GameObject object) {
        int loc = objects.indexOf(object);

        if (loc <= 0) return;

        Collections.swap(objects, loc, loc - 1);
    }

    public void dispose() {
        for (GameObject objects : objects) {
            for (Component components : objects.getComponents()) {
                components.dispose();
            }
        }
    }

    public void setPos(int x, int y) {
        GLFW.glfwSetWindowPos(window, x, y);
    }
}
