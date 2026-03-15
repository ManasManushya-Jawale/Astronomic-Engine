package org.astroEngine.Viewports;

import org.astroEngine.events.CallbackListener;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

public class DefaultViewport implements CallbackListener {
    @Override
    public void callback(long window) {
        GLFWWindowSizeCallback resize = GLFW.glfwSetWindowSizeCallback(window, (windowHandle, width, height) -> {
            GL11.glViewport(0, 0, width, height);
        });
    }
}
