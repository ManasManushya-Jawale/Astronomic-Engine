package org.astroEngine.comp.phy;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.std.TransformComponent;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.annot.RequiresComponent;
import org.joml.Vector3d;

@RequiresComponent(TransformComponent.class)
public class PhysicsComponent extends Component {

    public Vector3d position;
    public Vector3d velocity;
    public Vector3d acceleration;

    // Physical values
    private Long mass = 1L;

    public PhysicsComponent() {
        this.position = new Vector3d();
        this.velocity = new Vector3d();
        this.acceleration = new Vector3d(); // initialize with gravity
    }
    @Override
    public void update(float delta) {
        super.update(delta);

    }

    @Override
    public void setParent(GameObject parent) {
        super.setParent(parent);
        TransformComponent transform = (TransformComponent) parent.getComponent(TransformComponent.class);
        position.set(transform.transform.getTranslation(new Vector3d())); // sync initial position
    }

    // Utility methods for external forces
    public void applyForce(Vector3d force) {
        // F = m * a, assume unit mass for simplicity
        acceleration.add(force);
    }

    public void resetForces() {
        acceleration.set(0, 0, 0);
    }

    public void setAcceleration(Vector3d newAcceleration) {
        this.acceleration.set(newAcceleration);
    }

    public Vector3d getPosition() { return new Vector3d(position); }
    public Vector3d getVelocity() { return new Vector3d(velocity); }
    public Vector3d getAcceleration() { return new Vector3d(acceleration); }

    public Long getMass() {
        return mass;
    }

    public void setMass(Long mass) {
        this.mass = mass;
    }


}
