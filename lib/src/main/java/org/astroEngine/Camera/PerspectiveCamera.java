package org.astroEngine.Camera;

import org.astroEngine.shapes.GameObject;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class PerspectiveCamera extends GameObject {
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

    public Matrix4d getCombinedProjection() {
        return new Matrix4d(projection)
                .perspective(FOV, aspect, near, far)
                .translate(position).rotate(rotation);
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

    public void rotate(float x, float y, float z) {
        rotation.rotateXYZ(x, y, z);
    }

    public void rotate(Quaterniond q) {
        rotation.rotateXYZ(q.x, q.y, q.z);
    }

    public void moveDir(Vector3d dir, float v) {
        Vector3d dirVec = new Vector3d(dir).rotate(rotation);
        position.fma(v, dirVec);
    }
}
