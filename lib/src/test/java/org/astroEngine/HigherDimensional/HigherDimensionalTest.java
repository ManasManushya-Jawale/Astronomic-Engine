package org.astroEngine.HigherDimensional;

import imgui.ImGui;
import org.astroEngine.Camera.PerspectiveCamera;
import org.astroEngine.GUI.ImGUIObject;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.comp.Component;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.events.Shaders.ShaderProgramAdapter;
import org.astroEngine.events.Shaders.TextureProgramListener;
import org.astroEngine.graphics.geometry.Text;
import org.astroEngine.graphics.shaders.VertexShader;
import org.astroEngine.graphics.geometry.Cube;
import org.astroEngine.graphics.geometry.Sphere;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.AEMath;
import org.astroEngine.util.Astrodx;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.AEWindow;
import org.astroEngine.util.Files;
import org.joml.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

import java.awt.*;
import java.lang.Math;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HigherDimensionalTest extends AEWindow {

    private final DrawableObject myObject;
    private final DrawableObject cube;
    DrawableObject floor;
    DrawableObject text;

    private final Vector2d winSize = new Vector2d();
    private final Vector2d lastMouse = new Vector2d();

    private float yaw = 0, pitch = 0;
    private boolean firstMouse = true;

    private PerspectiveCamera camera;

    private Viewport viewport;

    boolean enabled = false;
    boolean leftMouse = false;

    ImGUIObject canvas;

    float time;

    public HigherDimensionalTest() {
        super(new Dimension(800, 600), "Higher Dimensional");

        setBackground(new Color(0, 5, 28));

        camera = new PerspectiveCamera((float) Math.toRadians(45), 800 / 600f, 0.1f, 100);
        camera.position.add(0, 0, -5);

        viewport = new Viewport(camera) {
            @Override
            public void apply(long window, int w, int h) {
                glViewport(0, 0, w, h);

                ((PerspectiveCamera) camera).perspective((float) Math.toRadians(45), ((float) w / h), 0.1f, 100);

                winSize.x = (float) w;
                winSize.y = (float) h;
            }
        };

        glfwSetWindowSizeCallback(window, (win, w, h) -> {
            viewport.apply(win, w, h);
            System.out.println(w + " x " + h);
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                leftMouse = action == GLFW_PRESS;
                if (leftMouse) firstMouse = true; // reset on click
            }
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

        canvas = new ImGUIObject(() -> {
            ImGui.begin("Canvas");
            ImGui.text("Hello World!");
            if (ImGui.beginMenu("My Menu")) {
                ImGui.text("My Text");
                ImGui.endMenu();
            }
            ImGui.end();
        });
        canvas.setLayer(0);

        time = 0;

        cube = new DrawableObject(new Cube(1, 1, 1));

        ((VertexShader) cube.getShape().getShape()).setShaderProgramListener(new TextureProgramListener(
                Files.internal("/image/TrappedSouls.png").getAbsolutePath()
        ) {{
            int x=1, y=4;
            this.uv = new float[]{
                    // FRONT (1,5,6) (1,6,2) — facing +Z
                    0, 0,  1, 0,  1, y,
                    0, 0,  1, y,  0, y,

                    // BACK (0,3,7) (0,7,4) — facing -Z
                    0, 0, 0, y, x, y,
                    0, 0, x, y, x, 0,

                    // LEFT (0,1,2) (0,2,3) — facing -X
                    0, 0,  1, 0,  1, y,
                    0, 0,  1, y,  0, y,

                    // RIGHT (4,7,6) (4,6,5) — facing +X
                    0, 0, 0, y, x, y,
                    0, 0, x, y, x, 0,

                    // BOTTOM (0,4,5) (0,5,1) — facing -Y
                    0, 0,  1, 0,  1, 1,
                    0, 0,  1, 1,  0, 1,

                    // TOP (3,2,6) (3,6,7) — facing +Y
                    0, 0,  1, 0,  1, 1,
                    0, 0,  1, 1,  0, 1,


            };
        }});
        ((VertexShader) cube.getShape().getShape()).setInternalSources("/shaders/Texture/Texture.vert", "/shaders/Texture/Texture.frag");
        cube.getTransform().translate(20, 0, 2);
        cube.setLayer(2);
        cube.getTransform().scale(5, 20, 5);
        addObject(cube);

        // ---------- myObject ----------
        myObject = new DrawableObject(new Sphere(1, 20, 20));
        ((VertexShader) myObject.getShape().getShape()).setShaderProgramListener(
                new ShaderProgramAdapter() {

                    @Override
                    public int applyParams(int s) {
                        int tLoc = glGetUniformLocation(s, "time");
                        glUniform1f(tLoc, time);
                        return s;
                    }
                });
        setShader(myObject, "/shaders/Gradient/Gradient.frag", "/shaders/Gradient/Gradient.vert");

        myObject.getTransformComponent().setScale(new Vector3d(1, -1, 1));

        addObject(myObject);

        addObject(canvas);

        text = new DrawableObject(new Text("""
                This is my personal hell, here no
                one cheers for peace. Everyone burns
                in this abyss forever with no way
                to burn his pain physically and he
                has to feel it mentally every time
                with everytime and they never stop
                feeling the torture due to my systems
                that refreshes their mind every minute
                \s""",
                Files.internal("/fonts/TNR.ttf")));

        text.getTransform().translate(15.25, 19, 7.1);
        text.getTransform().scale(.57f);

        addObject(text);
    }

    public void setShader(GameObject object, String frag, String vert) {
        ((VertexShader) object.getComponent(ShapeComp.class).getShape()).setVertexShaderSource(
                Files.readFile(Files.internal(vert))
        );
        ((VertexShader) object.getComponent(ShapeComp.class).getShape()).setFragmentShaderSource(
                Files.readFile(Files.internal(frag))
        );
    }

    @Override
    public void preWindowInitialization() {
        super.preWindowInitialization();
    }

    @Override
    public void loopSetup() {
        super.loopSetup();

        glEnable(GL_DEPTH_TEST);

        // Compile scene objects
        Astrodx.compileAllAvailableShaderObjects(this);

        viewport.apply(window, 800, 600);
        canvas.initGui("#version 330", true);
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        canvas.newFrame();
        super.draw();
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

        time += delta;
    }

    float speed = 20;

    public void moveCamera(Vector3d dir, double delta) {
        camera.moveDir(dir, (float) delta * speed);
    }

    public static void main(String[] args) {
        new HigherDimensionalTest().initialStart();
    }
}