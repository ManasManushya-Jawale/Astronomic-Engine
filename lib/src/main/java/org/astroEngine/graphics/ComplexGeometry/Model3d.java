package org.astroEngine.graphics.ComplexGeometry;

import org.astroEngine.Primitives.Objects.DrawableObject;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.events.Shaders.TextureProgramListener;
import org.astroEngine.graphics.Drawable;
import org.astroEngine.graphics.Shape;
import org.astroEngine.graphics.shaders.VertexShader;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.util.Files;
import org.joml.Matrix4d;
import org.jspecify.annotations.NonNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.Pointer;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class Model3d extends GameObject {


    public Model3d(String path, int values) {
        super();
        AIScene scene = aiImportFile(path, values);
        int meshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < meshes; i++) {
            System.out.println("Iteration 1: " + i);
            load(scene, i);
        }
    }

    public void load(AIScene scene, int i) {
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Float> uvMap = new ArrayList<>();
        ArrayList<Float> resultUV = new ArrayList<>();

        String texturePath = "";
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));

        int materialN = scene.mNumMaterials();
        System.out.println("materialN: " + materialN);
        PointerBuffer mat = scene.mMaterials();

        int matIndex = mesh.mMaterialIndex();
        AIMaterial material = AIMaterial.create(mat.get(matIndex));
        AIString matPath = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, matPath, ((IntBuffer) null), null, null, null, null, null);
        texturePath = matPath.dataString();

        AIVector3D.Buffer verts = mesh.mVertices();
        int nVert = mesh.mNumVertices();

        for (int j = 0; j < nVert; j++) {
            AIVector3D vert = verts.get(j);

            vertices.add(vert.x());
            vertices.add(vert.y());
            vertices.add(vert.z());
        }

        AIFace.Buffer faces = mesh.mFaces();

        for (AIFace face : faces) {
            indices.add(face.mIndices().get(0));
            indices.add(face.mIndices().get(1));
            indices.add(face.mIndices().get(2));
        }

        AIVector3D.Buffer uvChannel = mesh.mTextureCoords(0); // channel 0

        if (uvChannel == null) {
            System.out.println("No UV coords in channel 0");
            return;
        }

        int numVertices = mesh.mNumVertices();
        for (int j = 0; j < numVertices; j++) {
            AIVector3D uv = uvChannel.get(j);
            float u = uv.x();
            float v = uv.y();

            uvMap.add(u);
            uvMap.add(v);
        }

        ArrayList<Float> result = new ArrayList<>();

        for (
                int v : indices) {
            result.add(vertices.get(v * 3));
            result.add(vertices.get(v * 3 + 1));
            result.add(vertices.get(v * 3 + 2));
        }

        for (
                int v : indices) {
            resultUV.add(uvMap.get(v * 2));
            resultUV.add(uvMap.get(v * 2 + 1));
        }

        VertexShader meshObj = getVertexShader(result, texturePath, resultUV);

        addComponent(new ShapeComp(meshObj));
    }

    private static @NonNull VertexShader getVertexShader(ArrayList<Float> result, String texturePath, ArrayList<Float> resultUV) {
        VertexShader meshObj = new VertexShader("""
                #version 330 core
                
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec2 aTexCoord;
                
                out vec2 texCoord;
                
                uniform mat4 transform;
                
                void main() {
                    texCoord = aTexCoord;
                    gl_Position = transform * vec4(aPos, 1.0);
                }
                """,
                """
                        #version 330 core
                        
                        in vec2 texCoord;
                        out vec4 FragColor;
                        
                        uniform sampler2D tex;
                        uniform vec2 size = vec2(1, 1);
                        
                        void main() {
                            FragColor = texture(tex, texCoord*size);
                        }
                        """) {{

            SHAPE_TYPE = GL11.GL_TRIANGLES;

            setVertices(result);
        }};

        if (!texturePath.isEmpty()) {
            meshObj.
                    setShaderProgramListener(new TextureProgramListener(texturePath) {
                        {
                            flip = false;
                            uv = new float[resultUV.size()];
                            for (int i = 0; i < resultUV.size(); i++) {
                                uv[i] = resultUV.get(i);
                            }
                        }
                    });
        }
        return meshObj;
    }
}
