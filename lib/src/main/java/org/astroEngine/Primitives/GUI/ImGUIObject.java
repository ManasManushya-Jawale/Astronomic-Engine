package org.astroEngine.Primitives.GUI;

import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import org.astroEngine.graphics.Shape;
import org.astroEngine.shapes.GameObject;
import org.joml.Matrix4d;
import org.lwjgl.opengl.WGLEXTCreateContextES2Profile;

import java.awt.*;

public class ImGUIObject extends GameObject {
    private Runnable action;

    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;
    private ImGuiIO io;

    public ImGUIObject(Runnable draw) {
        this.action = draw;

        addDrawable(new Shape(Color.WHITE) {
            @Override
            public void draw(Matrix4d transform) {
                action.run();

                render();
            }
        });
    }

    public void initGui(String glslVersion, boolean instantCallback) {
        ImGui.createContext();

        io = ImGui.getIO();

        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();

        imGuiGlfw.init(getParent().window, instantCallback);
        imGuiGl3.init(glslVersion);
    }

    public void newFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void render() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
