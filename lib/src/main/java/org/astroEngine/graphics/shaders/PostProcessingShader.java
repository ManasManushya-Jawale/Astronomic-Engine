package org.astroEngine.graphics.shaders;

import org.astroEngine.comp.ShapeComp;
import org.astroEngine.graphics.Shape;
import org.joml.Matrix4d;

import java.awt.*;

public class PostProcessingShader extends Shape {
    String fragmentShaderSource;
    int fragmentShader;

    public PostProcessingShader(Color color) {
        super(color);
    }

    @Override
    public void draw(Matrix4d transform) {
    }
}
