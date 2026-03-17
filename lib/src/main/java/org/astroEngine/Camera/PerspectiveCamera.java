package org.astroEngine.Camera;

import org.astroEngine.shapes.GameObject;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class PerspectiveCamera extends Camera {
    private Matrix4d projection = new Matrix4d();

    private float FOV;
    private float aspect;
    private float near;
    private float far;

    private boolean zZeroToOne;

    public Vector3d position;
    public Quaterniond rotation;

    public PerspectiveCamera() {
        super();

        position = new Vector3d();
        rotation = new Quaterniond();
    }

    public PerspectiveCamera(float FOV, float aspect, float near, float far) {
        super();

        this.FOV = FOV;
        this.aspect = aspect;
        this.near = near;
        this.far = far;


        position = new Vector3d();
        rotation = new Quaterniond();
    }

    public void perspective(float fov, float aspect, float near, float far) {
        this.FOV = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
    }

    public Matrix4d getCombinedProjection() {
        return new Matrix4d(projection)
                .perspective(FOV, aspect, near, far).rotate(rotation)
                .translate(position);
    }

    public Matrix4d getProjection() {
        return projection;
    }

    public void setProjection(Matrix4d projection) {
        this.projection = projection;
    }

    public boolean iszZeroToOne() {
        return zZeroToOne;
    }

    public void setzZeroToOne(boolean zZeroToOne) {
        this.zZeroToOne = zZeroToOne;
    }

    public float getFOV() {
        return FOV;
    }

    public void setFOV(float FOV) {
        this.FOV = FOV;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }
// Inside PerspectiveCamera.java

    public void rotate(float x, float y, float z) {
        // Only update the orientation of the camera
        rotation.rotateXYZ(x, y, z);
    }

    public void moveDir(Vector3d dir, float speed) {
        // 1. Take the input direction (e.g., 0, 0, -1 for forward)
        Vector3d direction = new Vector3d(dir);

        // 2. Rotate that direction vector by the camera's current rotation
        direction.rotate(new Quaterniond(rotation).invert());

        // 3. Scale by speed and add to current position
        position.add(direction.mul(speed));
    }

    public void rotate(Quaterniond q) {
        rotate((float) q.x, (float) q.y, (float) q.z);
    }
}
