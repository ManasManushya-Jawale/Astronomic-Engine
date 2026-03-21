package org.astroEngine.physics.Simple;

import org.astroEngine.Primitives.Objects.PhysicsWorldObject;
import org.astroEngine.comp.Component;
import org.astroEngine.comp.PhysicsComponent;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.Shape;
import org.astroEngine.util.GameUtils;
import org.astroEngine.AEWindow;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.util.ArrayList;

public class SimplePhysics extends AEWindow {
    GameObject gameObject;
    ArrayList<Vector3d> vertexes;
    public SimplePhysics() {
        super(new Dimension(800, 600), "Simple Physics");

        getProjectionMatrix().ortho(0, 800, 600, 0, -1, 1);

        setBackground(Color.black);

        gameObject = new GameObject();
        gameObject.transform.setPosition(new Vector3d(200, 100,0));
        gameObject.addComponent(new ShapeComp(new Shape(Color.WHITE) {
            @Override
            public void draw(Matrix4d transform) {
                GameUtils.clearState();
                GameUtils.applyColor(Color.WHITE);
                GameUtils.applyTransforms(transform);
                glBegin(GL_POLYGON);
                float radius = 50;
                for (int theta=0 ;theta<360;theta++) {
                    double sin = Math.sin(Math.toRadians(theta));
                    double cos = Math.cos(Math.toRadians(theta));

                    glVertex2d(cos*radius, sin*radius);
                }
                glEnd();
            }
        }));
        gameObject.addComponent(new PhysicsComponent());
        gameObject.getComponent(PhysicsComponent.class).applyForce(new Vector3d(7.5, -10, 0));

        PhysicsComponent physicsComponent = gameObject.getComponent(PhysicsComponent.class);
        physicsComponent.setMass(1L);

        addObject(gameObject);

        gameObject.addComponent(new Component(){
            @Override
            public void update(float delta) {
                super.update(delta);

                PhysicsComponent physicsComponent = gameObject.getComponent(PhysicsComponent.class);

                float speed = 10;

                if (getParent().getParent().keyPressed(GLFW.GLFW_KEY_W)) {
                    physicsComponent.applyForce(new Vector3d(0, -speed, 0));
                }
                if (getParent().getParent().keyPressed(GLFW.GLFW_KEY_A)) {
                    physicsComponent.applyForce(new Vector3d(-speed, 0, 0));
                }
                if (getParent().getParent().keyPressed(GLFW.GLFW_KEY_S)) {
                    physicsComponent.applyForce(new Vector3d(0, speed, 0));
                }
                if (getParent().getParent().keyPressed(GLFW.GLFW_KEY_D)) {
                    physicsComponent.applyForce(new Vector3d(speed, 0, 0));
                }
            }
        });

        vertexes = new ArrayList<>();

        addObject(new PhysicsWorldObject(new Vector3d(0, 10, 0)));

        addObject(new GameObject(){{
            getTransformComponent().getTransform().translate(new Vector3d(200,200,0));
            addComponent(new ShapeComp(new Shape(Color.GRAY) {
                @Override
                public void draw(Matrix4d transform) {
                    GameUtils.clearState();
                    GameUtils.applyColor(color);
                    GameUtils.applyTransforms(transform);

                    glBegin(GL_LINE_STRIP);
                    for (Vector3d vertex : vertexes) {
                        glVertex2d(vertex.x, vertex.y);
                    }
                    glEnd();
                }
            }));
        }});
    }

    @Override
    public void loop(double fps) {
        super.loop(fps);

        vertexes.add(gameObject.getTransformComponent().transform.getTranslation(new Vector3d()));
    }


    public static void main(String[] args) {
        new SimplePhysics().initialStart();
    }
}
