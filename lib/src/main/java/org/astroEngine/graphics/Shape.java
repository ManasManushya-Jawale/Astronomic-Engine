package org.astroEngine.graphics;

import java.awt.Color;

import org.joml.Matrix4d;

public abstract class Shape implements Drawable {
    public Color color;

    public Shape(Color color) {
        this.color = color;
    }

    public abstract void draw(Matrix4d transform);
    
}
