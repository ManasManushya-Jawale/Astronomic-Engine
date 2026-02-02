package astronomicengine.util.Builder;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TextComponent;

import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import astronomicengine.comp.Component;
import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.comp.std.TransformComponent;
import astronomicengine.graphics.Graphics2DSprite;
import astronomicengine.shapes.GameObject;
import astronomicengine.shapes.Shape;
import astronomicengine.util.Math.AEMath;

public class GameObjectBuilder {
    private GameObject object;

    public GameObjectBuilder() {
        object = new GameObject();
    }

    public GameObjectBuilder(GameObject object) {
        this.object = object;
    }

    public GameObjectBuilder addComponent(Component c) {
        object.addComponent(c);
        return this;
    }

    public GameObjectBuilder setTranslate(Vector3d pos) {
        object.getComponent(TransformComponent.class).setPosition(pos);
        return this;
    }

    public GameObjectBuilder setRotation(Vector3d rot) {
        object.getComponent(TransformComponent.class).setRotation(rot);
        return this;
    }

    public GameObjectBuilder setScale(Vector3d scale) {
        object.getComponent(TransformComponent.class).setScale(scale);
        return this;
    }

    public GameObjectBuilder addDrawable(Shape shape) {
        addComponent(new DrawableComponent(shape));
        return this;
    }

    public static interface GraphicsScript {
        public void applyScript(Graphics2D g2d, Rectangle rect);
    }

    public GameObjectBuilder addGraphics(GraphicsScript script, Dimension size) {
        Graphics2DSprite g2s = new Graphics2DSprite(size.width, size.height);

        Graphics2D g2d = g2s.getGraphics();
        try {
            // Let the script draw into the sprite
            script.applyScript(g2d, new Rectangle(0, 0, size.width, size.height));
        } finally {
            g2d.dispose(); // free native resources
        }

        g2s.uploadTexture();
        addDrawable(g2s);

        return this;
    }

    public GameObject build() {
        return object;
    }
}
