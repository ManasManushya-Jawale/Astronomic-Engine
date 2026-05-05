package org.astroEngine.GUI;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;

import java.io.File;

/**
 * Initialization should always happen in loopSetup after ImGuiObject.init
 */
public class TTFont {
    private ImFont font;
    private float size;
    private ImFontConfig config;

    public TTFont() {
        font = new ImFont();
        size = 16;
        config = new ImFontConfig();
    }

    public TTFont(ImFont font) {
        this.font = font;
        size = font.getFontSize();
        config = new ImFontConfig();
    }

    public TTFont(File ttfFile) {
        size = 16;
        config = new ImFontConfig();
        font = ImGui.getIO().getFonts().addFontFromFileTTF(ttfFile.getAbsolutePath(), size);
    }

    public TTFont(File ttfFile, float size) {
        this.size = size;
        config = new ImFontConfig();
        font = ImGui.getIO().getFonts().addFontFromFileTTF(ttfFile.getAbsolutePath(), size, config);
    }

    public TTFont(File ttfFile, float size, ImFontConfig config) {
        size = 16;
        this.config = config;
        font = ImGui.getIO().getFonts().addFontFromFileTTF(ttfFile.getAbsolutePath(), size, config);
    }

    public ImFont getFont() {
        return font;
    }

    public void setFont(ImFont font) {
        this.font = font;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public ImFontConfig getConfig() {
        return config;
    }

    public void setConfig(ImFontConfig config) {
        this.config = config;
    }
}
