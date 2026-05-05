package org.astroEngine.Assimp;

import org.astroEngine.AEWindow;
import org.astroEngine.Camera.OrthographicCamera;
import org.astroEngine.Camera.PerspectiveCamera;
import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.Viewports.Viewport;
import org.astroEngine.events.Shaders.TextureProgramListener;
import org.astroEngine.graphics.ComplexGeometry.Model3d;
import org.astroEngine.graphics.shaders.VertexShader;
import org.astroEngine.util.AEMath;
import org.astroEngine.util.Astrodx;
import org.astroEngine.util.Builder.Shapes;
import org.astroEngine.util.Files;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.assimp.Assimp.*;

public class AssimpTest extends AEWindow {
  PerspectiveCamera camera=new PerspectiveCamera(((float)AEMath.toRadians(45)),800/600f,.1f,100f){{moveDir(new Vector3d(0,0,-1),10);}};

  int width, height;

  boolean focus = true;

  Viewport viewport=new Viewport(camera){float minWorldWidth=800;float minWorldHeight=600;

  float worldWidth,worldHeight;int vpX,vpY,vpWidth,vpHeight;

  @Override public void apply(long window,int screenWidth,int screenHeight){float screenAspect=(float)screenWidth/screenHeight;float worldAspect=minWorldWidth/minWorldHeight;

  if(screenAspect>worldAspect){
  // Screen is wider → extend world width
  worldHeight=minWorldHeight;worldWidth=minWorldHeight*screenAspect;}else{
  // Screen is taller → extend world height
  worldWidth=minWorldWidth;worldHeight=minWorldWidth/screenAspect;}

  // Viewport fills the entire window, no black bars
  vpX=0;vpY=0;vpWidth=screenWidth;vpHeight=screenHeight;

  width=screenWidth;height=screenHeight;

  GL11.glViewport(vpX,vpY,vpWidth,vpHeight);

  // Update your projection matrix to match the extended world size
  if(camera instanceof OrthographicCamera)((OrthographicCamera)camera).setOrtho(-worldWidth/2,worldWidth/2,-worldHeight/2,worldHeight/2,0.1f,100f);else if(camera instanceof PerspectiveCamera)((PerspectiveCamera)camera).setAspect(((float)screenWidth/screenHeight));}};

  double delta = 0;

  Model3d cube;

  AIScene scene;

  public AssimpTest() {
    super(new Dimension(800, 600), "My Window");

    setViewport(viewport);
    setBackground(Color.black);

    glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
        glfwSetInputMode(window, GLFW_CURSOR,
            mouseState ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
        mouseState = !mouseState;
        glfwSetCursorPos(window, width / 2f, height / 2f);
      }
    });

    glfwSetWindowFocusCallback(window, (windowHandle, wasFocused) -> {
      this.focus = wasFocused;
      System.out.println("Focused: " + focus);
    });

    {
      DrawableObject obj = Shapes.cube(this, 3, 5, .1f);
      rotate(obj.getTransform(), 45);
      ((VertexShader) obj.getShape().getShape())
          .setShaderProgramListener(new TextureProgramListener(
              Files.internal("/image/bear5.png").getAbsolutePath()) {
            {
              int x = 1;
              int y = 1;
              uv = new float[] {
                  // FRONT (1,5,6) (1,6,2) — facing +Z
                  0, 0, 1, 0, 1, y,
                  0, 0, 1, y, 0, y,

                  // BACK (0,3,7) (0,7,4) — facing -Z
                  0, 0, 0, y, x, y,
                  0, 0, x, y, x, 0,

                  // LEFT (0,1,2) (0,2,3) — facing -X
                  0, 0, 1, 0, 1, y,
                  0, 0, 1, y, 0, y,

                  // RIGHT (4,7,6) (4,6,5) — facing +X
                  0, 0, 0, y, x, y,
                  0, 0, x, y, x, 0,

                  // BOTTOM (0,4,5) (0,5,1) — facing -Y
                  0, 0, 1, 0, 1, 1,
                  0, 0, 1, 1, 0, 1,

                  // TOP (3,2,6) (3,6,7) — facing +Y
                  0, 0, 1, 0, 1, 1,
                  0, 0, 1, 1, 0, 1,

          };
            }
          });

      ((VertexShader) obj.getShape().getShape()).setInternalSources("/shaders/Texture/Texture.vert",
          "/shaders/Texture/Texture.frag");
    }

    cube = new Model3d(
        Files.internal("/objs/Me.obj").getAbsolutePath(),
            aiProcess_Triangulate | aiProcess_CalcTangentSpace |
            aiProcess_PreTransformVertices | aiProcess_FlipUVs);
    cube.getTransform().translateLocal(0, 0, 5).scale(20);
    addObject(cube);
  }

  @Override
  public void loopSetup() {
    super.loopSetup();

    Astrodx.compileAllAvailableShaderObjects(this);

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    viewport.apply(window, 800, 600);

  }

  @Override
  public void draw() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    super.draw();
  }

  boolean mouseState = true;

  @Override
  public void loop(double fps) {
    super.loop(fps);
    setProjectionMatrix(camera.getCombinedProjection());
    this.delta = 1 / fps;

    if (focus) {
      double[] xpos = new double[1], ypos = new double[1];
      glfwGetCursorPos(window, xpos, ypos);

      float relX = (float) ((xpos[0] / width) * 100f);
      if (relX < 10 || relX > 90) {
        camera.rotation.rotateY(delta * (relX < 10 ? -1 : 1));
      }
    }
  }

  public void rotate(Matrix4d mat, double theta) {
    double rad = Math.toRadians(theta);
    mat.rotateAround(new Quaterniond().rotateY(rad), -camera.position.x, -camera.position.y,
        -camera.position.z);
  }

  public static void main(String[] args) {
    new AssimpTest().initialStart();
  }
}
