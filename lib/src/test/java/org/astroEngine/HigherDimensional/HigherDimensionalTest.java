package org.astroEngine.HigherDimensional;

import imgui.ImGui;
import org.astroEngine.Camera.PerspectiveCamera;
import org.astroEngine.Primitives.GUI.ImGUIObject;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.ShaderSprite;
import org.astroEngine.graphics.geometry.Cube;
import org.astroEngine.graphics.geometry.Sphere;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.AEWindow;
import org.astroEngine.util.Files;
import org.joml.*;

import static java.awt.Color.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.lang.Math;

public class HigherDimensionalTest extends AEWindow {

    private final GameObject myObject;
    private final GameObject cube;

    private final Vector2d winSize = new Vector2d();
    private final Vector2d lastMouse = new Vector2d();

    private float yaw = 0, pitch = 0;
    private boolean firstMouse = true;

    private PerspectiveCamera camera;

    private Viewport viewport;

    boolean enabled = false;
    boolean leftMouse = false;

    ImGUIObject canvas;

    public HigherDimensionalTest() {
        super(new Dimension(800, 600), "Higher Dimensional");

        setBackground(BLACK);

        camera = new PerspectiveCamera((float) Math.toRadians(45), 800 / 600f, 0.1f, 100);
        camera.position.add(0, 0, -5);

        viewport = new Viewport(camera) {
            @Override
            public void apply(long window, int w, int h) {
                glViewport(0, 0, w, h);

                camera.perspective((float) Math.toRadians(45), ((float) w / h), 0.1f, 100);

                winSize.x = (float) w;
                winSize.y = (float) h;
            }
        };

        glfwSetWindowSizeCallback(window, (win, w, h) -> {
            viewport.apply(win, w, h);
            System.out.println(w + " x " + h);
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            leftMouse = button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS;
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if (!leftMouse) return;

            if (firstMouse) {
                lastMouse.set(xpos, ypos);
                firstMouse = false;
            }

            double dx = xpos - lastMouse.x;
            double dy = ypos - lastMouse.y;

            lastMouse.set(xpos, ypos);

            float sensitivity = 0.002f;

            yaw += (float) (-dx * sensitivity);
            pitch += (float) (-dy * sensitivity);

            // Clamp pitch
            pitch = (float) Math.max(-Math.toRadians(85), Math.min(Math.toRadians(85), pitch));

            camera.rotation.identity()
                    .rotateX(-pitch)
                    .rotateY(-yaw);
        });

        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetInputMode(window, GLFW_CURSOR, enabled ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
                enabled = !enabled;
            }
        });

        cube = new GameObjectBuilder()
                .setTranslate(new Vector3d(2, 2, 0))
                .addDrawable(new Cube(4, 4, 4))
                .build();

        canvas = new ImGUIObject(() -> {
            ImGui.begin("Canvas");
            ImGui.text("Hello World!");
            if (ImGui.button("Manas", 175, 125) && !objects.contains(cube)) {
                addObject(cube);
            }
            if (ImGui.beginMenu("My Menu")) {
                ImGui.text("My Text");
                ImGui.endMenu();
            }
            ImGui.end();
        });


        // ---------- myObject ----------
        myObject = new DrawableObject(new Sphere(1, 20, 20));
        ((ShaderSprite) myObject.getComponent(ShapeComp.class).getShape()).setVertexShaderSource(
                Files.readFile(Files.internal("/shaders/Colors/Saturated/Saturated.vert"))
        );
        ((ShaderSprite) myObject.getComponent(ShapeComp.class).getShape()).setFragmentShaderSource(
                Files.readFile(Files.internal("/shaders/Colors/Saturated/Saturated.frag"))
        );
        myObject.getTransformComponent().setScale(new Vector3d(1, -1, 1));

        addObject(myObject);

        addObject(canvas);
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        canvas.newFrame();

        super.draw();

    }

    @Override
    public void preWindowInitialization() {
        super.preWindowInitialization();

    }

    @Override
    public void loopSetup() {
        glEnable(GL_DEPTH_TEST);

        ((ShaderSprite) myObject.getComponent(ShapeComp.class).getShape()).compile();
        ((ShaderSprite) cube.getComponent(ShapeComp.class).getShape()).compile();

        viewport.apply(window, 800, 600);

        canvas.initGui("#version 330", true);
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        setProjectionMatrix(camera.getCombinedProjection());

        float delta = ((float) (1 / fps));

        if (keyPressed(GLFW_KEY_W)) moveCamera(new Vector3d(0, 0, 1), delta);
        if (keyPressed(GLFW_KEY_S)) moveCamera(new Vector3d(0, 0, -1), delta);

        if (keyPressed(GLFW_KEY_D)) moveCamera(new Vector3d(-1, 0, 0), delta);
        if (keyPressed(GLFW_KEY_A)) moveCamera(new Vector3d(1, 0, 0), delta);

        if (keyPressed(GLFW_KEY_SPACE)) camera.position.fma(delta * speed, new Vector3d(0, -1, 0));
        if (keyPressed(GLFW_KEY_LEFT_SHIFT)) camera.position.fma(delta * speed, new Vector3d(0, 1, 0));

        if (objects.contains(cube)) {
            pushBack(cube);
        }
    }

    float speed = 20;

    public void moveCamera(Vector3d dir, double delta) {
        camera.moveDir(dir, (float) delta * speed);
    }

    public static void main(String[] args) {
        new HigherDimensionalTest().initialStart();
    }
}