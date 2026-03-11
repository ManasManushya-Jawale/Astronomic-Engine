package org.astroEngine.HigherDimensional;

import org.apache.commons.math3.complex.Quaternion;
import org.astroEngine.comp.Component;
import org.astroEngine.comp.std.DrawableComponent;
import org.astroEngine.graphics.ShaderSprite;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.shapes.Shape;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.util.FileUtils;
import org.astroEngine.util.GameUtils;
import org.astroEngine.window.AEWindow;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

import static java.awt.Color.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HigherDimensionalTest extends AEWindow {

    private final float[] A = {-1, -1, -1};
    private final float[] B = {-1, -1, 1};
    private final float[] C = {-1, 1, -1};
    private final float[] D = {-1, 1, 1};
    private final float[] E = {1, -1, -1};
    private final float[] F = {1, -1, 1};
    private final float[] G = {1, 1, -1};
    private final float[] H = {1, 1, 1};
    private final GameObject cube;

    private Vector3d cameraPosition = new Vector3d(0, 0, -5);
    private Quaterniond cameraRotation = new Quaterniond();

    public HigherDimensionalTest() {
        super(new Dimension(800, 600), "Higher Dimensional");

        setBackground(BLACK);

        // ---------- Projection (lens only) ----------
        getProjectionMatrix().identity().perspective(
                (float) Math.toRadians(45),
                800f / 600f,
                0.1f,
                100.0f
        );
        getProjectionMatrix().translate(0, 0, -5);

        cameraRotation = new Quaterniond();

        ArrayList<Float> points = getCubeVertices();

        // ---------- Cube ----------
        cube = new GameObjectBuilder()
                .addDrawable(new ShaderSprite(
                        GL_TRIANGLES,
                        points,
                        FileUtils.readFile(FileUtils.internal("/shaders/Colors/Vertex.glsl")),
                        FileUtils.readFile(FileUtils.internal("/shaders/Colors/Frag.glsl"))
                ))
                .build();

        addObject(cube);

        // ---------- Camera ----------
        objects.add(new GameObjectBuilder()
                .addComponent(new Component() {
                    @Override
                    public void update(float delta) {
                        float speed = 2f;
                        cameraPosition = new Vector3d();
                        cameraRotation = new Quaterniond();

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

    }

    private @NonNull ArrayList<Float> getCubeVertices() {
        ArrayList<Float> verts = new ArrayList<>();

        float[][] vertices = {A, B, C, D, E, F, G, H};

        // Each face: two triangles (triangle list)
        int[][] faces = {
                {0, 2, 6, 4},
                {6, 4, 5, 7},
                {5, 2, 3, 7},
                {7, 3, 1, 5},
                {0, 2, 3, 1},
                {0, 4, 5, 1}
        };

        for (int[] f : faces) {
            // Triangle 1
            verts.add(vertices[f[0]][0]); verts.add(vertices[f[0]][1]); verts.add(vertices[f[0]][2]);
            verts.add(vertices[f[1]][0]); verts.add(vertices[f[1]][1]); verts.add(vertices[f[1]][2]);
            verts.add(vertices[f[2]][0]); verts.add(vertices[f[2]][1]); verts.add(vertices[f[2]][2]);

            // Triangle 2
            verts.add(vertices[f[0]][0]); verts.add(vertices[f[0]][1]); verts.add(vertices[f[0]][2]);
            verts.add(vertices[f[2]][0]); verts.add(vertices[f[2]][1]); verts.add(vertices[f[2]][2]);
            verts.add(vertices[f[3]][0]); verts.add(vertices[f[3]][1]); verts.add(vertices[f[3]][2]);
        }

        return verts;
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

        ((ShaderSprite) cube.getComponent(DrawableComponent.class).getShape()).compile();
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);
        cube.getTransformComponent().transform.rotateY(1 / fps * 2);
        cube.getTransformComponent().transform.rotateX(1 / fps * 1.25f);
//        cube.transform.transform.translate(0, 0, -5 / fps);
        getProjectionMatrix().translate(cameraPosition);
        getProjectionMatrix().rotate(cameraRotation);
    }

    public static void main(String[] args) {
        new HigherDimensionalTest().initialStart();
    }
}