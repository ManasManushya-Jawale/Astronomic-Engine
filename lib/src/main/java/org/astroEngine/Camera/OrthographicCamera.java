package org.astroEngine.Camera;

import org.astroEngine.comp.Component;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.AEWindow;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class OrthographicCamera extends Camera {
  private Vector3d nearBounds, farBounds;
  private Matrix4d projection;

  public OrthographicCamera(AEWindow parent, Vector3d nearBounds, Vector3d farBounds) {
    super();
    setParent(parent);

    this.nearBounds = nearBounds;
    this.farBounds = farBounds;
    this.projection = new Matrix4d();

    initBindings();
  }

  public OrthographicCamera(AEWindow parent, double l, double r, double b, double t, double n, double f) {
    super();
    setParent(parent);

    setOrtho(l, r, b, t, n, f);

    this.projection = new Matrix4d();

    initBindings();
  }

  private void initBindings() {
    updateOrtho();
    addComponent(new Component() {
      @Override
      public void update(float delta) {
        super.update(delta);

        getParent().getParent().getProjectionMatrix().set(projection);
      }
    });
  }

  public void updateOrtho() {
    projection.ortho(nearBounds.x, farBounds.x, farBounds.y, nearBounds.y, nearBounds.z, farBounds.z);
  }


  public void setOrtho(Vector3d nearBounds, Vector3d farBounds) {
    this.nearBounds = nearBounds;
    this.farBounds = farBounds;
  }

  public void setProjection(Matrix4d projection) {
    this.projection = projection;
  }

  public Matrix4d getProjection() {
    return projection;
  }

  public void setOrtho(double l, double r, double b, double t, double n, double f) {
    setOrtho(new Vector3d(l, t, n), new Vector3d(r, b, f));
  }
}
