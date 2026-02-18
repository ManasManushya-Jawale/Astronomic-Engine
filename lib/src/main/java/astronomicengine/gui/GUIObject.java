package astronomicengine.gui;

import astronomicengine.graphics.Graphics2DSprite;
import astronomicengine.shapes.GameObject;
import org.joml.Vector3d;

import java.awt.*;
import java.util.ArrayList;

/**
 * An object that shows GUI. This can be customized using its <code>'paintComponent'</code> method
 */
public class GUIObject extends GameObject {
    private Rectangle bounds;
    private Graphics2DSprite.GraphicsScript
            paintComponent,
            paintHover,
            paintClick,
            paintKey;

    public GUIObject(Rectangle bounds) {
        initComponentList();
        this.bounds = bounds;
    }

    public GUIObject(Rectangle bounds, Graphics2DSprite.GraphicsScript paintComponent, Graphics2DSprite.GraphicsScript paintHover, Graphics2DSprite.GraphicsScript paintClick, Graphics2DSprite.GraphicsScript paintKey) {
        this.bounds = bounds;
        this.paintComponent = paintComponent;
        this.paintHover = paintHover;
        this.paintClick = paintClick;
        this.paintKey = paintKey;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setSize(Dimension size) {
        bounds.setSize(size);
    }

    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }

    public Dimension getSize() {
        return bounds.getSize();
    }

    public void setPosition(Point position) {
        bounds.setLocation(position);
    }

    public void setPosition(int x, int y) {
        bounds.setLocation(x, y);
    }

    public Point getPosition() {
        return bounds.getLocation();
    }

    public Graphics2DSprite.GraphicsScript getPaintComponent() {
        return paintComponent;
    }

    public void setPaintComponent(Graphics2DSprite.GraphicsScript paintComponent) {
        this.paintComponent = paintComponent;
    }

    public Graphics2DSprite.GraphicsScript getPaintHover() {
        return paintHover;
    }

    public void setPaintHover(Graphics2DSprite.GraphicsScript paintHover) {
        this.paintHover = paintHover;
    }

    public Graphics2DSprite.GraphicsScript getPaintClick() {
        return paintClick;
    }

    public void setPaintClick(Graphics2DSprite.GraphicsScript paintClick) {
        this.paintClick = paintClick;
    }

    public Graphics2DSprite.GraphicsScript getPaintKey() {
        return paintKey;
    }

    public void setPaintKey(Graphics2DSprite.GraphicsScript paintKey) {
        this.paintKey = paintKey;
    }

    /**
     * Paint the component using openGL api
     */
    public void paintComponent() {

    }
}
