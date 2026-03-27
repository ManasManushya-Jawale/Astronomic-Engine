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
import org.astroEngine.graphics.shaders.VertexShader;
import org.astroEngine.graphics.geometry.Cube;
import org.astroEngine.graphics.geometry.Sphere;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.AEMath;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.AEWindow;
import org.astroEngine.util.Files;
import org.joml.*;
import org.lwjgl.opengl.GL30;

import static java.awt.Color.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.lang.Math;

public class HigherDimensionalTest extends AEWindow {

    private final DrawableObject myObject;
    private final DrawableObject cube;

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

        setBackground(BLACK);

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
                Files.internal("/image/Water.png").getAbsolutePath()
        ) {{this.uv = new float[]{
                // LEFT (0,1,2) (2,3,0)
                0,0,  1,0,  1,1,
                1,1,  0,1,  0,0,

                // RIGHT (4,5,6) (6,7,4)
                0,0,  1,0,  1,1,
                1,1,  0,1,  0,0,

                // BOTTOM (0,1,5) (0,4,5)
                0,0,  1,0,  1,1,
                0,0,  0,1,  1,1,

                // TOP (2,3,7) (2,6,7)
                0,0,  1,0,  1,1,
                0,0,  0,1,  1,1,

                // FRONT (1,2,6) (1,5,6)
                0,0,  1,0,  1,1,
                0,0,  0,1,  1,1,

                // BACK (0,3,7) (0,4,7)
                0,0,  1,0,  1,1,
                0,0,  0,1,  1,1,
        };
        }});
        ((VertexShader) cube.getShape().getShape()).setVertexShaderSource("""
                    #version 330 core
                    
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec2 aTexCoord;
                    
                    out vec2 texCoord;
                    
                    uniform mat4 transform;
                    
                    void main() {
                        texCoord = aTexCoord;
                        gl_Position = transform * vec4(aPos, 1.0);
                    }
                    """);
        ;
        ((VertexShader) cube.getShape().getShape()).setFragmentShaderSource("""
                    #version 330 core
                    
                    in vec2 texCoord;
                    out vec4 FragColor;
                    
                    uniform sampler2D tex;
                    
                    void main() {
                        FragColor = texture(tex, texCoord);
                    }""");
        cube.getTransform().translate(0, 0, 2);
        cube.setLayer(2);
        addObject(cube);

        // ---------- myObject ----------
        myObject = new DrawableObject(new Sphere(1, 20, 20));
        ((VertexShader) myObject.getShape().getShape()).setShaderProgramListener(
        new ShaderProgramAdapter() {

            @Override
            public int applyParams(int s) {
                int tLoc = GL30.glGetUniformLocation(s, "time");
                GL30.glUniform1f(tLoc, time);
                return s;
            }
        });
        setShader(myObject, "/shaders/Gradient/Gradient.frag", "/shaders/Gradient/Gradient.vert");

        myObject.getTransformComponent().setScale(new Vector3d(1, -1, 1));

        addObject(myObject);

        addObject(canvas);
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

        ((VertexShader) myObject.getComponent(ShapeComp.class).getShape()).compile();
        ((VertexShader) cube.getShape().getShape()).compile();

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