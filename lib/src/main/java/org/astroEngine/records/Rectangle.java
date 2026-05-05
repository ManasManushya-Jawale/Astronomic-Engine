package org.astroEngine.records;

public record Rectangle(double x, double y, double width, double height) {
    public double intersection(Rectangle obj) {
        double top = Math.min(this.y, obj.y);
        double bottom = Math.max(this.y+height, obj.y+obj.height);
        double left = Math.min(this.x, obj.x);
        double right = Math.max(this.x+width, obj.x+obj.width);

        return (right - left) * (top - bottom);
    }

    public boolean intersects(Rectangle obj) {
        return intersection(obj) >= 0;
    }
}
