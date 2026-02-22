package org.astroEngine.comp.std;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.astroEngine.comp.Component;

public class TransformComponent extends Component {
    private Vector3d position;
    private Vector3d rotation; // radians (x,y,z)
    private Vector3d scale;
    private Vector3d pivot; // local pivot point
    public Matrix4d transform;

    public TransformComponent() {
        position = new Vector3d(0, 0, 0);
        rotation = new Vector3d(0, 0, 0);
        scale = new Vector3d(1, 1, 1);
        pivot = new Vector3d(0, 0, 0);
        transform = new Matrix4d().identity();
    }

    /** Reset to identity */
    public void reset() {
        position.set(0, 0, 0);
        rotation.set(0, 0, 0);
        scale.set(1, 1, 1);
        pivot.set(0, 0, 0);
        transform.identity();
    }

    /** Compose matrix from pos/rot/scale/pivot */
    public void recalcMatrix() {
        transform.identity()
                .translate(position)
                .translate(pivot) // move to pivot
                .rotateXYZ(rotation.x, rotation.y, rotation.z)
                .scale(scale)
                .translate(-pivot.x, -pivot.y, -pivot.z); // move back
    }

    // --- Setters ---
    public void setPosition(Vector3d pos) {
        this.position.set(pos);
        recalcMatrix();
    }

    public void setRotation(Vector3d rot) {
        this.rotation.set(rot);
        recalcMatrix();
    }

    public void setScale(Vector3d scl) {
        this.scale.set(scl);
        recalcMatrix();
    }

    public void setPivot(Vector3d pivot) {
        this.pivot.set(pivot);
        recalcMatrix();
    }

    // --- Incremental transforms ---
    public void translate(Vector3d delta) {
        this.position.add(delta);
        recalcMatrix();
    }

    public void translateRelative(Vector3d delta) {
        this.transform.translate(delta);
        transform.getTranslation(position);
        recalcMatrix();
    }

    public void rotate(Vector3d delta) {
        this.rotation.add(delta);

        recalcMatrix();
    }

    public void scale(Vector3d factor) {
        this.scale.mul(factor);
        recalcMatrix();
    }

    // --- Accessors ---
    public Vector3d getPosition() {
        return new Vector3d(position);
    }

    public Vector3d getRotation() {
        return new Vector3d(rotation);
    }

    public Vector3d getScale() {
        return new Vector3d(scale);
    }

    public Vector3d getPivot() {
        return new Vector3d(pivot);
    }

    public Matrix4d getTransform() {
        return new Matrix4d(transform);
    }

    public void setTransform(Matrix4d mat) {
        this.transform.set(mat);
    }

    public void rotateZ(float f) {
        rotate(new Vector3d(0, 0, f));
    }
}
