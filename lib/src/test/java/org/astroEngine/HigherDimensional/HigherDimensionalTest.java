package org.astroEngine.HigherDimensional;

import org.astroEngine.Camera.PerspectiveCamera;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.comp.DrawableComponent;
import org.astroEngine.graphics.ShaderSprite;
import org.astroEngine.graphics.geometry.Cube;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.util.FileUtils;
import org.astroEngine.AEWindow;
import org.joml.Vector2d;
import org.joml.Vector3d;

import static java.awt.Color.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.util.ArrayList;

public class HigherDimensionalTest extends AEWindow {

    private final GameObject myObject;
    private final GameObject cube;

    private PerspectiveCamera camera;

    private Viewport viewport;

    public HigherDimensionalTest() {
        super(new Dimension(800, 600), "Higher Dimensional");

        setBackground(BLACK);

        camera = new PerspectiveCamera((float) Math.toRadians(45), 800/600f, 0.1f, 100);
        camera.position.add(0, 0, -5);

        viewport = new Viewport(camera) {
            @Override
            public void apply(long window, int w, int h) {
                glViewport(0, 0, w, h);

                ((PerspectiveCamera) camera).perspective((float) Math.toRadians(45), ((float)w/h), 0.1f, 100);
            }
        };

        glfwSetWindowSizeCallback(window, (win, w ,h) -> {
            viewport.apply(win, w, h);
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            System.out.println("Mouse moved: " + xpos + ", " + ypos);
        });

        ArrayList<Float> points = generateSphere(1, 20, 20);

        // ---------- myObject ----------
        myObject = new GameObjectBuilder()
                .addDrawable(new ShaderSprite(
                        GL_TRIANGLES,
                        points,
                        FileUtils.readFile(FileUtils.internal("/shaders/Colors/Vertex.glsl")),
                        FileUtils.readFile(FileUtils.internal("/shaders/Colors/Frag.glsl"))
                ))
                .build();

        addObject(myObject);


        cube = new GameObjectBuilder()
                .setTranslate(new Vector3d(2, 2, 0))
                .addDrawable(new Cube(4, 4, 4))
                .build();

        addObject(cube);
    }

    public static ArrayList<Float> generateSphere(float radius, int stacks, int sectors) {
        ArrayList<Float> verts = new ArrayList<>();

        verts.add(0f);
        verts.add(-10f);
        verts.add(0f);
        verts.add(0f);
        verts.add(10f);
        verts.add(0f);
        verts.add(.1f);
        verts.add(-10f);
        verts.add(0f);

        for (int i = 0; i < stacks; i++) {

            double stackAngle1 = Math.PI / 2 - i * Math.PI / stacks;
            double stackAngle2 = Math.PI / 2 - (i + 1) * Math.PI / stacks;

            double xy1 = radius * Math.cos(stackAngle1);
            double y1 = radius * Math.sin(stackAngle1);

            double xy2 = radius * Math.cos(stackAngle2);
            double y2 = radius * Math.sin(stackAngle2);

            for (int j = 0; j < sectors; j++) {

                double sector1 = j * 2 * Math.PI / sectors;
                double sector2 = (j + 1) * 2 * Math.PI / sectors;

                float x1 = (float)(xy1 * Math.cos(sector1));
                float z1 = (float)(xy1 * Math.sin(sector1));

                float x2 = (float)(xy2 * Math.cos(sector1));
                float z2 = (float)(xy2 * Math.sin(sector1));

                float x3 = (float)(xy2 * Math.cos(sector2));
                float z3 = (float)(xy2 * Math.sin(sector2));

                float x4 = (float)(xy1 * Math.cos(sector2));
                float z4 = (float)(xy1 * Math.sin(sector2));

                // triangle 1
                verts.add(x1); verts.add((float)y1); verts.add(z1);
                verts.add(x2); verts.add((float)y2); verts.add(z2);
                verts.add(x3); verts.add((float)y2); verts.add(z3);

                // triangle 2
                verts.add(x1); verts.add((float)y1); verts.add(z1);
                verts.add(x3); verts.add((float)y2); verts.add(z3);
                verts.add(x4); verts.add((float)y1); verts.add(z4);
            }
        }

        return verts;
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.draw();
    }

    @Override
    public void preWindowInitialization() {
        super.preWindowInitialization();
    }

    @Override
    public void loopSetup() {
        glEnable(GL_DEPTH_TEST);

        ((ShaderSprite) myObject.getComponent(DrawableComponent.class).getShape()).compile();
        ((ShaderSprite) cube.getComponent(DrawableComponent.class).getShape()).compile();
    }

    Vector2d mousePos = new Vector2d();

    @Override
    public void loop(double fps) {
        super.loop(fps);
        setProjectionMatrix(camera.getCombinedProjection());

        float delta = ((float) (1 / fps));
        float rotation = 5;

        if (keyPressed(GLFW_KEY_W)) moveCamera(new Vector3d(0, 0, 1), delta);
        if (keyPressed(GLFW_KEY_A)) camera.rotate(0, -delta*rotation, 0);
        if (keyPressed(GLFW_KEY_S)) moveCamera(new Vector3d(0, 0, -1), delta);
        if (keyPressed(GLFW_KEY_D)) camera.rotate(0, delta*rotation, 0);

    }

    float speed = 20;

    public void moveCamera(Vector3d dir, double delta) {
        camera.moveDir(dir, (float)delta * speed);
    }

    public static void main(String[] args) {
        new HigherDimensionalTest().initialStart();
    }
}