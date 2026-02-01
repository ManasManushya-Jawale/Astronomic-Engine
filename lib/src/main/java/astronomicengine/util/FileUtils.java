package astronomicengine.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUtils {
    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static File internal(String resourcePath) {
        Class<?> caller = WALKER.walk(frames -> frames.skip(1).findFirst().get().getDeclaringClass());
        URL url = caller.getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found");
        }
        String path = "";
        try {
            path = new File(url.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new File(path);
    }
}