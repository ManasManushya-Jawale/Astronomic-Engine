package org.astroEngine.util;

import org.astroEngine.graphics.shaders.VertexShader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Files {
    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static File internal(String resourcePath) {
        Class<?> caller = WALKER.walk(frames -> frames.skip(1).findFirst().get().getDeclaringClass());
        URL url = caller.getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        try {
            return new File(url.toURI()).getAbsoluteFile();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid resource URI: " + resourcePath, e);
        }
    }

    public static String readFile(File file) {
        try {
            return java.nio.file.Files.readString(file.toPath());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static VertexShader getShaderSprite(File frag, File vert, List<Float> points) {
        String fragStr = readFile(frag);
        String vertStr = readFile(vert);
        return new VertexShader(
                fragStr, vertStr, points
        );
    }
}
