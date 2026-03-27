package org.astroEngine.events.Shaders;

public interface ShaderProgramListener {
    int applyParams(int shaderProgram);
    int onCompile(int shaderProgram);
    int onCompletion(int shaderProgram);
    void preDraw();
    void postDraw();
    int afterCompile(int shaderProgram);
}
