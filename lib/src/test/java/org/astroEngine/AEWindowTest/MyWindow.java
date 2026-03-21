package org.astroEngine.AEWindowTest;

import java.awt.*;
import java.util.Arrays;

import org.astroEngine.Camera.OrthographicCamera;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Primitives.Objects.ImageObject;
import org.astroEngine.Viewports.ScaleViewport;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.ShaderSprite;
import org.astroEngine.util.FileUtils;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Graphics2DSprite;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.AEWindow;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MyWindow extends AEWindow {

    private final DrawableObject shaderObj;
    public GameObject car;
    public GameObject button;

    public OrthographicCamera camera;
    public ScaleViewport viewport;

    public float speed = 3/2f;
    public float steer = 1;

    public float v = 0;

    public MyWindow() {
        super(new Dimension(800, 600), "My Custom Window");

        camera = new OrthographicCamera(this, 0, 800, 600, 0, -1, 1);
        addObject(camera);

        viewport = new ScaleViewport(camera);
        setViewport(viewport);

        shaderObj = (DrawableObject) new GameObjectBuilder(new DrawableObject(new ShaderSprite(
                FileUtils.readFile(FileUtils.internal("/shaders/pulsing/Vertex.glsl")),
                FileUtils.readFile(FileUtils.internal("/shaders/pulsing/Frag.glsl")),
                Arrays.asList(
                        -50f, -50f, 0f,
                        50f, -50f, 0f,
                        50f, 50f, 0f,
                        -50f, 50f, 0f
                )
        ){{
            setShaderProgramListener(shaderProgram -> {
                glUniform1f(glGetUniformLocation(shaderProgram, "time"), v);
                glUniform1f(glGetUniformLocation(shaderProgram, "u_scale"), 10);
                return shaderProgram;
            });
        }}))
                .setTranslate(new Vector3d(0, 0, 0)) //translation
                .build();
        addObject(shaderObj);

        car = new GameObjectBuilder(new ImageObject(FileUtils.internal("/cars/HayaBabu.png"), true))
                .setTranslate(new Vector3d(400, 300, 0)) //translation
                .setScale(new Vector3d(6)) // scaling factor
                .build(); // finalizes the builder to give the Game Object

        addObject(car); // adds the object into the object list

        Graphics2DSprite.GraphicsScript script = (g2d, w, h) -> {
            g2d.setColor(Color.BLUE);
            g2d.fillRoundRect(0, 0, w, h, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Helo", 20, 30);
            return g2d;
        };

        Graphics2DSprite g2s = new Graphics2DSprite(100, 100, script);

        button = new GameObjectBuilder()
                .setTranslate(new Vector3d(100, 100, 0))
                .setRotation(new Vector3d(0, 0, 1))
                .addDrawable(g2s)
                .build();
//        addObject(button);


    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        Vector3d oldPos = car.getTransformComponent().getPosition();
        Vector3d oldRot = car.getTransformComponent().getRotation();

        if (keyPressed(GLFW.GLFW_KEY_W)) {
            car.transform.translateRelative(new Vector3d(0, -speed, 0));
        }
        if (keyPressed(GLFW.GLFW_KEY_A)) {
            car.getTransformComponent().rotateZ((float) (-steer / fps));
        }
        if (keyPressed(GLFW.GLFW_KEY_S)) {
            car.getTransformComponent().translateRelative(new Vector3d(0, speed, 0));
        }
        if (keyPressed(GLFW.GLFW_KEY_D)) {
            car.getTransformComponent().rotateZ((float) (steer / fps));
        }

        Vector3d newPos = car.getTransformComponent().getPosition();
        Vector3d newRot = car.getTransformComponent().getRotation();

        Vector3d d = oldPos.sub(newPos, new Vector3d());
        Vector3d dR = oldRot.sub(newRot, new Vector3d());
        camera.getProjection().translate(d);

        camera.getProjection().rotateAround(new Quaterniond().rotateZ(dR.z), newPos.x, newPos.y, newPos.z);

        v+= (float) (1f/fps);
    }

    @Override
    public void loopSetup() {
        super.loopSetup();
        glDisable(GL_DEPTH_TEST);
        ((ShaderSprite) shaderObj.getComponent(ShapeComp.class).getShape()).compile();
    }

    public static void main(String[] arg) throws Exception {
        if (!GLFW.glfwInit())
            throw new Exception();

        MyWindow window = new MyWindow();
        window.initialStart();
    }
}
