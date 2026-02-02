package astronomicengine.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.joml.Matrix4d;

import astronomicengine.graphics.img.ImageSprite;
import astronomicengine.shapes.Shape;

/**
 * A bridge between AWT Graphics2D drawing and LWJGL rendering.
 * Uses ImageSprite internally for OG-style rendering.
 */
public class Graphics2DSprite extends Shape {

    private BufferedImage image;
    private ImageSprite delegate; // reuse ImageSprite for rendering

    public int width, height;

    public Graphics2DSprite(int width, int height) {
        super(Color.WHITE);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        this.height = height;
        this.width = width;

        // Initial delegate
        delegate = new ImageSprite(image);
    }

    /** Get a Graphics2D object to draw into the image. */
    public Graphics2D getGraphics() {
        return image.createGraphics();
    }

    /** Upload the current BufferedImage to OpenGL as a texture. */
    public void uploadTexture() {
        // Refresh delegate with updated image
        delegate.dispose(); // free old texture
        delegate = new ImageSprite(image);
    }

    /** Render using ImageSprite's OG draw logic. */
    @Override
    public void draw(Matrix4d transform) {
        if (delegate == null) {
            delegate = new ImageSprite(image);
        }
        delegate.draw(transform);
    }

    /** Dispose texture resources. */
    public void dispose() {
        if (delegate != null) {
            delegate.dispose();
            delegate = null;
        }
    }

    /** Reset the backing BufferedImage to a new size. */
    public void resetImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        // Refresh delegate
        if (delegate != null) {
            delegate.dispose();
        }
        delegate = new ImageSprite(image);
    }
}
