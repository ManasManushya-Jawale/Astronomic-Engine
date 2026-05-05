package org.astroEngine.tutorial.SimpleGame;

import imgui.ImDrawList;
import imgui.internal.ImGui;
import org.astroEngine.AEWindow;
import org.astroEngine.Camera.PerspectiveCamera;
import org.astroEngine.Constants.vSyncState;
import org.astroEngine.GUI.ImGUIObject;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Viewports.BoxViewport;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.comp.Component;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.events.Shaders.TextureProgramListener;
import org.astroEngine.graphics.shaders.TextureShader;
import org.astroEngine.graphics.shaders.VertexShader;
import org.astroEngine.graphics.geometry.Cube;
import org.astroEngine.graphics.geometry.Sphere;
import org.astroEngine.util.AEMath;
import org.astroEngine.util.Astrodx;
import org.astroEngine.util.Files;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Main extends AEWindow {

    PerspectiveCamera camera;
    Viewport viewport;

    DrawableObject player;

    DrawableObject floor, floor2, floor3, floor4;

    AEMath.Rect3d playerRect;

    ArrayList<DrawableObject> obstacles = new ArrayList<>();

    ImGUIObject canvas;

    float cubeSpeedCap = 5;
    int score = 0;

    float playerSpeedCap = 10;

    public Main() {
        super(new Dimension(600, 600), "A Simple Game");
        setBackground(new Color(0, 0, 0, 0));
        setVSync(vSyncState.ENABLE);

        camera = new PerspectiveCamera(((float) Math.toRadians(25)), 800 / 600f, 0.1f, 100f);
        camera.rotate(((float) Math.toRadians(15)), ((float) Math.toRadians(180)), 0);
        camera.moveDir(new Vector3d(0, 0, 1), -10);
        addObject(camera);

        viewport = new BoxViewport(camera);
        setViewport(viewport);

        player = new DrawableObject(new Sphere(1, 20, 20));
        player.addComponent(new Component() {
            @Override
            public void update(float delta) {
                super.update(delta);

                if (playerSpeedCap < 1) {
                    playerSpeedCap = 1;
                }

                if (keyPressed(GLFW.GLFW_KEY_D)) {
                    player.getTransform().translateLocal(delta * AEMath.generateRandomFloat(0, -playerSpeedCap), 0, 0);
                    playerSpeedCap -= 0.001f;
                }

                if (keyPressed(GLFW.GLFW_KEY_A)) {
                    player.getTransform().translateLocal(delta * AEMath.generateRandomFloat(0, playerSpeedCap), 0, 0);
                    playerSpeedCap -= 0.001f;
                }

                player.getTransform().rotateX(Math.toRadians(500 * delta));

                Vector3d pos = new Vector3d();
                player.getTransform().getTranslation(pos);

                playerRect = new AEMath.Rect3d(
                        new Vector3d(pos).sub(1, 1, 1),
                        new Vector3d(pos).add(1, 1, 1));

                if (playerRect.min.x < -4) {
                    player.getTransform().translateLocal(0.1f, 0, 0);
                }

                if (playerRect.max.x > 4) {
                    player.getTransform().translateLocal(-0.1f, 0, 0);
                }
                player.getTransform().getTranslation(pos);

                camera.position
                        .zero()
                        .add(new Vector3d(pos).negate());
                camera.rotation.identity();
                camera.rotate((float) Math.toRadians(15), ((float) Math.toRadians(180)), 0);
                camera.moveDir(new Vector3d(0, 0, 1), -10);
            }
        });
        addObject(player);

        floor = new DrawableObject(new TextureShader(Files.internal("/image/Water.png").getAbsolutePath()));
        ((TextureShader) floor.getShape().getShape()).setVerticesArr(new float[]{
                // pos            // uv
                0, 0, 0, 0f, 0f,
                1, 0, 0, 1, 0,
                1, 0, 1, 1, 1,
                0, 0, 1, 0, 1
        });
        floor.getTransform().scale(8, 1, 25);
        floor.getTransform().translateLocal(-4, -1, -5);
        ((TextureShader) floor.getShape().getShape()).SHAPE_TYPE = GL_QUADS;
        addObject(floor);

        floor2 = floor.clone();
        floor2.getTransform().translateLocal(8, 0, 0);
        floor2.getTransform().rotateZ(Math.toRadians(90));
        floor2.getTransform().scale(4, 1, 1);
        addObject(floor2);

        floor3 = floor2.clone();
        floor3.getTransform().translateLocal(-8, 0, 0);
        addObject(floor3);

        floor4 = floor.clone();
        floor4.getTransform().identity().scale(8, 4, 1);
        floor4.getTransform().translateLocal(-4, -1, 20);
        floor4.getTransform().rotateX(Math.toRadians(-90));
        addObject(floor4);

        canvas = new ImGUIObject(() -> {
            ImDrawList draw = ImGui.getForegroundDrawList();
            draw.addText(0, 0, 0xFFFFFFFF, "Score: " + score);
        });
        addObject(canvas);
    }

    @Override
    public void loopSetup() {
        super.loopSetup();
        glEnable(GL_DEPTH_TEST);

        Astrodx.compileAllAvailableShaderObjects(this);

        viewport.apply(window, 600, 600);

        canvas.initGui("#version 330", true);
    }

    @Override
    public void beforeDraw() {
        super.beforeDraw();
        canvas.newFrame();

    }

    float currTime = 0;
    float timer = 1;

    ArrayList<DrawableObject> removeObstacle = new ArrayList<>();

    @Override
    public void loop(double fps) {

        super.loop(fps);
        System.out.println(fps);
        setProjectionMatrix(camera.getCombinedProjection());

        float delta = ((float) (1 / fps));

        currTime += delta;

        if (currTime > timer) {
//            System.out.println("Adding a cube");

            DrawableObject cube = new DrawableObject(new Cube(AEMath.generateRandomFloat(.1f, 1), AEMath.generateRandomFloat(.1f, 1), AEMath.generateRandomFloat(.1f, 1)));

            ((VertexShader) cube.getShape().getShape()).setShaderProgramListener(new TextureProgramListener(
                    Files.internal("/image/Water.png").getAbsolutePath()
            ) {{
                this.uv = new float[]{
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
            ((VertexShader) cube.getShape().getShape()).compile();
            cube.getTransform().translate(AEMath.generateRandomFloat(-2.5f, 2.5f), 0, 10);
            cube.addComponent(new Component() {
                @Override
                public void update(float delta) {
                    super.update(delta);

                    Vector3d pos = new Vector3d();
                    parent.getTransform().getTranslation(pos);

                    if (pos.z < -5) {
                        removeObject(parent);
                        score -= 2;
                    }

                    parent.getTransform().translateLocal(0, 0, -delta * AEMath.generateRandomFloat(0.1f, cubeSpeedCap));
                    parent.getTransform().rotateLocalX(AEMath.toRadians(delta));
                }
            });
            cube.setLayer(2);
            obstacles.add(cube);
            addObject(cube);

            currTime = 0;
            timer = AEMath.generateRandomFloat(.5f, 1);
        }

        obstacles.removeAll(removeObstacle);
        for (DrawableObject obstacle : obstacles) {

            Vector3d p1 = new Vector3d();
            obstacle.getTransform().getTranslation(p1);
            AEMath.Rect3d rect = new AEMath.Rect3d(
                    p1,
                    new Vector3d(p1).add(
                            ((Cube) obstacle.getShape().getShape()).getL(),
                            ((Cube) obstacle.getShape().getShape()).getB(),
                            ((Cube) obstacle.getShape().getShape()).getW())
            );

            if (AEMath.intersects(rect, playerRect)) {
                cubeSpeedCap += 0.001f;
                score += 1;
                removeObject(obstacle);
                removeObstacle.add(obstacle);
            }
        }

        canvas.render();
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.draw();
    }

    public static void main(String[] args) {
        new Main().initialStart();
    }
}
