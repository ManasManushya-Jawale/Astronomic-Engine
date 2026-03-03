package org.astroEngine.Primitives.Objects.phy;

import org.astroEngine.comp.Component;
import org.astroEngine.comp.phy.PhysicsComponent;
import org.astroEngine.comp.phy.World;
import org.astroEngine.comp.std.TransformComponent;
import org.astroEngine.shapes.GameObject;
import org.joml.Vector3d;

public class PhysicsWorldObject extends GameObject {
    public final double GRAVITATIONAL_CONSTANT = 6.6743 * Math.pow(10, -11);

    public PhysicsWorldObject(Vector3d gravity) {
        super();

        addComponent(new Component() {
            @Override
            public void update(float delta) {
                for (int i = 0; i < parent.getParent().objects.size(); i++) {
                    PhysicsComponent pc1;
                    TransformComponent tc1;
                    tc1 = parent.getParent().objects.get(i).getComponent(TransformComponent.class);
                    if ((pc1 = parent.getParent().objects.get(i).getComponent(PhysicsComponent.class)) != null) {

                        pc1.acceleration.fma(delta, gravity);
                        // Step 3: integrate velocity0
                        pc1.velocity.fma(delta, pc1.acceleration); // v += a * dt

                        // Step 4: integrate position
                        pc1.position.fma(delta, pc1.velocity);     // p += v * dt

                        // Step 5: update transform
                        tc1.transform.setTranslation(pc1.position);
                    }
                }
            }
        });

    }
}
