package org.astroEngine.SolarSystem;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.astroEngine.AEWindow;
import org.astroEngine.Camera.OrthographicCamera;
import org.astroEngine.GUI.ImGUIObject;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Viewports.BoxViewport;
import org.astroEngine.graphics.ImageSprite;
import org.astroEngine.graphics.Shape;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.Files;
import org.astroEngine.util.Astrodx;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Main extends AEWindow {
    public GameObject sun, moon, earth;

    public double r1 = 340, t1 =0;
    public double r2 = 50, t2 = 0;

    public ArrayList<Vector3d> earthPoints, moonPoints;

    public OrthographicCamera camera;

    public float s1 = 100, s2 = 100/50f;
    public float rs1 = 90, rs2 = 90, rs3 = 90;

    public ImGUIObject canvas;

    public Vector3d earthPos, moonPos;

    public Main() {
        super(new Dimension(800, 800), "SolarSystem");
        setBackground(Color.BLACK);

        camera =new OrthographicCamera(this, 0, 800, 800, 0, -1, 1);
        addObject(camera);

        setViewport(new BoxViewport(camera));

        earthPoints = new ArrayList<>();
        moonPoints = new ArrayList<>();

        sun = new DrawableObject(new ImageSprite(Files.internal("/Sun.png")));
        earth = new DrawableObject(new ImageSprite(Files.internal("/Earth.png")));
        moon = new DrawableObject(new ImageSprite(Files.internal("/Moon.png")));

        sun.getTransform().translate(400, 400, 0);

        moon.getTransform().scale(1/25f*1/1.125f);

        earth.getTransform().scale(1/25f);

        addObject(new DrawableObject(new Shape(Color.WHITE) {
            @Override
            public void draw(Matrix4d transform) {
                Astrodx.clearState();
                Astrodx.applyColor(color);
                Astrodx.applyTransforms(transform);

                GL11.glBegin(GL11.GL_LINE_STRIP);
                for (Vector3d point : earthPoints) {
                    GL11.glVertex3d(point.x, point.y, point.z);
                }
                GL11.glEnd();

                GL11.glBegin(GL11.GL_LINE_STRIP);
                for (Vector3d point : moonPoints) {
                    GL11.glVertex3d(point.x, point.y, point.z);
                }
                GL11.glEnd();

                Astrodx.clearState();
            }
        }));

        addObject(sun);
        addObject(earth);
        addObject(moon);

        canvas = new ImGUIObject(() -> {
            ImGui.begin("Enter Simulation data");

            ImFloat s1 = new ImFloat(this.s1), s2 = new ImFloat(this.s2);

            if (input("Earth Speed", s1)) {
                this.s1 = s1.get();
            }

            if (input("Moon Speed", s2)) {
                this.s2 = s2.get();
            }

            ImFloat rs1 = new ImFloat(this.rs1), rs2 = new ImFloat(this.rs2), rs3 = new ImFloat(this.rs3);
            if (input("Earth's r speed:", rs1)) {
                this.rs1 = rs1.get();
            }
            if (input("Moon's r speed:", rs2)) {
                this.rs2 = rs2.get();
            }
            if (input("Sun's r speed:", rs3)) {
                this.rs3 = rs3.get();
            }

            if (ImGui.button("Reset position")) {
                t1 = 0;
                t2 = 0;
            }

            if (ImGui.button("Reset Points")) {
                earthPoints.clear();
                moonPoints.clear();
            }

            ImGui.end();
        });

        addObject(canvas);
    }

    @Override
    public void loopSetup() {
        super.loopSetup();
        canvas.initGui("#version 330", true);
    }

    @Override
    public void draw() {
        canvas.newFrame();
        super.draw();
    }

    @Override
    public void loop(double fps) {

        super.loop(fps);

        float delta = ((float) (1/fps));

        if (objects.contains(moon)) {
            earthPos = new Vector3d(Math.cos(t1) * r1, Math.sin(t1) * r1, 0);
            earthPos.add(400, 400, 0);
            earth.getTransform().setTranslation(earthPos.x, earthPos.y, earthPos.z);

            earth.getTransform().rotateZ(Math.toRadians(delta * rs1));
            moon.getTransform().rotateZ(Math.toRadians(delta * rs2));
            sun.getTransform().rotateZ(Math.toRadians(delta * rs3));

            moonPos = new Vector3d(Math.cos(t2) * r2, Math.sin(t2) * r2, 0);
            moonPos.add(earthPos);
            moon.getTransform().setTranslation(moonPos);

            earthPoints.add(earthPos);
            moonPoints.add(moonPos);

            t1 += Math.toRadians((delta) * s1);
            t2 += Math.toRadians((delta) * s2);
        }
    }

    public static void main(String[] args) {
        new Main().initialStart();
    }

    public boolean input(String str, ImFloat value) {
        ImGui.text(str);
        ImGui.sameLine(150);
        return ImGui.inputFloat("##" + str, value);
    }
}
