package org.astroEngine.gui.ImGUI;
import imgui.ImFont;
import imgui.ImGuiIO;
import imgui.internal.ImGui;
import org.astroEngine.AEWindow;
import org.astroEngine.GUI.ImGUIObject;
import org.astroEngine.util.Files;

import java.awt.*;

public class ImGUITest extends AEWindow {
    ImGUIObject object;
    ImFont font;

    public ImGUITest() {
        super(new Dimension(800, 600), "ImGui");

        object = new ImGUIObject(() -> {

            ImGui.begin("MyWindow");

            ImGui.text("Manas");

            ImGui.pushFont(font);
            ImGui.text("JetBrains");
            ImGui.popFont();

            ImGui.end();
        });
        addObject(object);
    }

    @Override
    public void loopSetup() {
        super.loopSetup();

        object.initGui("#version 330", true);

        ImGuiIO io = object.getIo();

        font = io.getFonts().addFontFromFileTTF(
                Files.internal("/fonts/JetBrainsMono.ttf").getAbsolutePath(), 24
        );

        // 🔥 VERY IMPORTANT
        object.getImGuiGl3().updateFontsTexture();

        ImGui.styleColorsDark();
    }

    @Override
    public void draw() {
        object.newFrame();
        super.draw();
    }

    public static void main(String[] args) {
        new ImGUITest().initialStart();
    }

}