package org.astroEngine.graphics.shaders;

import org.astroEngine.events.Shaders.ShaderProgramAdapter;
import org.astroEngine.events.Shaders.ShaderProgramListener;
import org.astroEngine.graphics.Shape;
import org.astroEngine.records.Shader;
import org.astroEngine.util.VertexInfo;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL40.glUniformMatrix4dv;

public class VertexShader extends Shape {

    int vertexShader;
    int fragmentShader;
    int shaderProgram;

    private String vertexShaderSource;
    private String fragmentShaderSource;

    private int transformLoc;
    VertexInfo info;

    private List<Float> vertices = new ArrayList<>();

    public int SHAPE_TYPE;

    private float[] verticesArr;

    public static final VertexShader DEFAULT_SHADER_SPRITE = new VertexShader(
            """
                    #version 330 core
                    
                    layout(location = 0) in vec3 aPos;
                    uniform mat4 transform;
                    
                    out vec3 pColor;
                    
                    void main() {
                        gl_Position = transform * vec4(aPos, 1.0);
                    
                        pColor = (aPos + 1) / 2;
                    }
                    """,
            """
                    #version 330 core
                    
                    out vec4 FragColor;
                    in vec3 pColor;
                    
                    void main() {
                    
                        FragColor = vec4(pColor, 1);
                    }"""
    );

    public static final Shader DEFAULT_SHADER = new Shader(
            """
                    #version 330 core
                    
                    out vec4 FragColor;
                    in vec3 pColor;
                    
                    void main() {
                    
                        FragColor = vec4(pColor, 1);
                    }""",
            """
                    #version 330 core
                    
                    layout(location = 0) in vec3 aPos;
                    uniform mat4 transform;
                    
                    out vec3 pColor;
                    
                    void main() {
                        gl_Position = transform * vec4(aPos, 1.0);
                    
                        pColor = (aPos + 1) / 2;
                    }
                    """
    );

    private ShaderProgramListener shaderProgramListener = new ShaderProgramAdapter();

    public VertexShader(VertexShader sprite) {
        super(Color.WHITE);

        String vertexShaderSource = sprite.vertexShaderSource;
        String fragmentShaderSource = sprite.fragmentShaderSource;

        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
        this.vertices = sprite.vertices;

        this.SHAPE_TYPE = GL_TRIANGLE_FAN;

        if (!sprite.vertices.isEmpty()) {
            this.verticesArr = new float[vertices.size()];

            for (int i = 0; i < vertices.size(); i++) {
                this.verticesArr[i] = vertices.get(i);
            }
        }else {
            this.verticesArr = new float[3];
        }
    }

    public VertexShader(Shader shader, List<Float> vertices, int type) {
        super(Color.WHITE);

        String vertexShaderSource = shader.vert();
        String fragmentShaderSource = shader.frag();

        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        this.SHAPE_TYPE = type;

        setVertices(vertices);
    }

    public VertexShader(String vertexShader, String fragmentShader, List<Float> vertices) {
        super(Color.WHITE);

        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;
        this.vertices = vertices;

        this.SHAPE_TYPE = GL_TRIANGLE_FAN;

        setVertices(vertices);
    }

    public VertexShader(String vertexShader, String fragmentShader) {
        super(Color.WHITE);

        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;

        this.SHAPE_TYPE = GL_TRIANGLES;

        this.verticesArr = new float[3];
    }

    public VertexShader(int SHAPE_TYPE, List<Float> vertices, String vertexShaderSource, String fragmentShaderSource) {
        super(Color.WHITE);
        this.SHAPE_TYPE = SHAPE_TYPE;
        this.vertices = vertices;
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        this.verticesArr = new float[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            this.verticesArr[i] = vertices.get(i);
        }

    }

    @Override
    public void draw(Matrix4d transform) {
        shaderProgramListener.preDraw();

        int VAO = info.VAO();
        int len = info.len();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        transform.get(buffer);

        transformLoc = glGetUniformLocation(shaderProgram, "transform");

        glBindVertexArray(VAO);
        glUseProgram(shaderProgram);
        applyShaderProgramListener(shaderProgramListener);
        glUniformMatrix4fv(transformLoc, false, buffer);
        glDrawArrays(SHAPE_TYPE, 0, len);

        shaderProgramListener.postDraw();
    }

    public void applyShader() {
        glBindVertexArray(info.VAO());
        glUseProgram(shaderProgram);
    }

    public void compile() {
        try {
            createShader(createVertexAttr());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public VertexInfo createVertexAttr() {
        int len = vertices.size() / 3;

        int vVBO = glGenBuffers();

        int VAO = glGenVertexArrays();

        glBindVertexArray(VAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, vVBO);
        glBufferData(GL_ARRAY_BUFFER, verticesArr, GL_STATIC_DRAW);

// 3. then set our vertex attributes pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        shaderProgramListener.afterCompile(shaderProgram);

        return (info = new VertexInfo(VAO, len));
    }

    private void createShader(VertexInfo info) throws URISyntaxException, IOException {
        int VAO = info.VAO();
        int len = info.len();

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        // Sourcing
        glShaderSource(vertexShader, vertexShaderSource);
        glShaderSource(fragmentShader, fragmentShaderSource);

        // Compiling
        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);

        // Attaching into a shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram); // Validating if there are any errors
        glUseProgram(shaderProgram);

        transformLoc = glGetUniformLocation(shaderProgram, "transform");

        int compileStatus = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (compileStatus == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(vertexShader));
        }

        compileStatus = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (compileStatus == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(fragmentShader));
        }
        shaderProgramListener.onCompile(shaderProgram);

        glUseProgram(0);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public String getFragmentShaderSource() {
        return fragmentShaderSource;
    }

    public void setFragmentShaderSource(String fragmentShaderSource) {
        this.fragmentShaderSource = fragmentShaderSource;
    }

    public String getVertexShaderSource() {
        return vertexShaderSource;
    }

    public void setVertexShaderSource(String vertexShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public int getFragmentShader() {
        return fragmentShader;
    }

    public void setFragmentShader(int fragmentShader) {
        this.fragmentShader = fragmentShader;
    }

    public int getVertexShader() {
        return vertexShader;
    }

    public void setVertexShader(int vertexShader) {
        this.vertexShader = vertexShader;
    }

    public void applyShaderProgramListener(ShaderProgramListener listener) {
        setShaderProgram(listener.applyParams(shaderProgram));
    }

    public ShaderProgramListener getShaderProgramListener() {
        return shaderProgramListener;
    }

    public void setShaderProgramListener(ShaderProgramListener shaderProgramListener) {
        this.shaderProgramListener = shaderProgramListener;
    }

    public float[] getVerticesArr() {
        return verticesArr;
    }

    public void setVerticesArr(float[] verticesArr) {
        this.verticesArr = verticesArr;
        this.vertices = new ArrayList<>();
        for (int i = 0; i < verticesArr.length; i++) {
            this.vertices.add(verticesArr[i]);
        }
    }

    public List<Float> getVertices() {
        return vertices;
    }

    public void setVertices(List<Float> vertices) {
        this.vertices = vertices;

        this.verticesArr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            this.verticesArr[i] = vertices.get(i);
        }
    }
}
