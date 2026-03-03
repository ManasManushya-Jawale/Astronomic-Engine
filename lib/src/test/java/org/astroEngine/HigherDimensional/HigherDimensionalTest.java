package org.astroEngine.HigherDimensional;

import org.astroEngine.comp.Component;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.shapes.Shape;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.util.GameUtils;
import org.astroEngine.window.AEWindow;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Random;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import static java.awt.Color.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

public class HigherDimensionalTest extends AEWindow {

    private final int[] A = {-1, -1, -1};
    private final int[] B = {-1, -1, 1};
    private final int[] C = {-1, 1, -1};
    private final int[] D = {-1, 1, 1};
    private final int[] E = {1, -1, -1};
    private final int[] F = {1, -1, 1};
    private final int[] G = {1, 1, -1};
    private final int[] H = {1, 1, 1};
    private final GameObject cube;

    private Vector3d cameraPosition = new Vector3d(0, 0, -5);
    private Quaterniond cameraRotation = new Quaterniond();

    public HigherDimensionalTest() {
        super(new Dimension(800, 600), "Higher Dimensional");

        setBackground(Color.BLACK);

        // ---------- Projection (lens only) ----------
        getProjectionMatrix().identity().perspective(
                (float) Math.toRadians(60),
                800f / 600f,
                0.1f,
                100.0f
        );
        getProjectionMatrix().translate(0, 0, -5);

        cameraRotation = new Quaterniond();

        // ---------- Cube ----------
        cube = new GameObjectBuilder()
                .addDrawable(new Shape(Color.WHITE) {
                    @Override
                    public void draw(Matrix4d model) {
                        GameUtils.clearState();
                        applyTransformations(model);

                        drawFace(MAGENTA, A, B, D, C); // left
                        drawFace(RED,     E, G, H, F); // right
                        drawFace(YELLOW,  A, E, F, B); // bottom
                        drawFace(CYAN,    C, D, H, G); // top
                        drawFace(BLUE,    A, C, G, E); // back
                        drawFace(GREEN,   B, F, H, D); // front
                    }
                })
                .build();

        addObject(cube);

        // ---------- Camera ----------
        objects.add(new GameObjectBuilder()
                .addComponent(new Component() {
                    @Override
                    public void update(float delta) {
                        float speed = 5f;

                        if (keyPressed(GLFW_KEY_W))
                            cameraPosition.z += speed * delta;

                        if (keyPressed(GLFW_KEY_S))
                            cameraPosition.z -= speed * delta;

                        if (keyPressed(GLFW_KEY_A))
                            cameraRotation.rotateY(-speed * delta);

                        if (keyPressed(GLFW_KEY_D))
                            cameraRotation.rotateY(speed * delta);
                    }
                })
                .build());

        cube.transform.setScale(new Vector3d(1.5));

    }
    private void drawFace(Color color, int[] v1, int[] v2, int[] v3, int[] v4) {
        GameUtils.applyColor(color);

        glBegin(GL_TRIANGLES);

        // Triangle 1
        glVertex3iv(v1);
        glVertex3iv(v2);
        glVertex3iv(v3);

        // Triangle 2
        glVertex3iv(v1);
        glVertex3iv(v3);
        glVertex3iv(v4);

        glEnd();
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
        glDepthFunc(GL_LESS);
        glClearDepth(1.0);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

    }

    @Override
    public void loop(double fps) {
        super.loop(fps);
        cube.getTransformComponent().transform.rotateY(1 / fps * 2);
        cube.getTransformComponent().transform.rotateX(1 / fps * 1.25f);
//        cube.transform.transform.translate(0, 0, -5 / fps);
//        getProjectionMatrix().translate(0, 0, -1 / fps);
    }

    public static void main(String[] args) {
        new HigherDimensionalTest().initialStart();
    }
}