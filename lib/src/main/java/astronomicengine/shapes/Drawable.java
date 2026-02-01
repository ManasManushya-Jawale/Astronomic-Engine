package astronomicengine.shapes;

import org.joml.Matrix3d;
import org.joml.Matrix4d;

public interface Drawable {
    public void draw(Matrix4d transform);
}
