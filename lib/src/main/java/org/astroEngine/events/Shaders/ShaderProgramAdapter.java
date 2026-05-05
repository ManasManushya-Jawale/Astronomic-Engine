package org.astroEngine.events.Shaders;

/**
 * Note: do not call ShaderProgram's super funtion when
 * extending it in your code.<br>
 * Do not:
 * <code>super.superFunc</code><br>
 * Do:
 * <code>directly write your code into the function</code><br>
 * this is because this function is a super implementation of shader program listener
 * where it requires to return a shader program
 */
public class ShaderProgramAdapter implements ShaderProgramListener {
    @Override
    public int applyParams(int shaderProgram) {
        return shaderProgram;
    }

    @Override
    public int onCompile(int shaderProgram) {
        return shaderProgram;
    }

    @Override
    public int onCompletion(int shaderProgram) {
        return shaderProgram;
    }

    @Override
    public void preDraw() { }
    @Override
    public void postDraw() { }

    @Override
    public int afterCompile(int shaderProgram) {
        return shaderProgram;
    }

}
