package org.astroEngine.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class FileUtils {
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
            return Files.readString(file.toPath());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
