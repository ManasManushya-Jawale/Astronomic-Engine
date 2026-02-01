package astronomicengine.util.Builder;

import java.awt.TextComponent;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import astronomicengine.comp.Component;
import astronomicengine.comp.std.TransformComponent;
import astronomicengine.shapes.GameObject;
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

    public GameObjectBuilder centerTransform(int width, int height) {
        TransformComponent tc = object.getComponent(TransformComponent.class);
        Vector3d pos = new Vector3d(), scl = new Vector3d(), rot = new Vector3d();
        pos = tc.getPosition();
        scl = tc.getScale();

        rot = tc.getRotation();

        // Extract Euler angles (in radians)
        double angleZ = rot.z;
        tc.setTransform(AEMath.buildCenteredTransform(pos.x, pos.y, width, height, angleZ, scl.x, scl.y));

        return this;
    }

    public GameObject build() {
        return object;
    }
}
