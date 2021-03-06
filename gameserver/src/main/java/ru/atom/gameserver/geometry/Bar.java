package ru.atom.gameserver.geometry;

/**
 * Created by gammaker on 01.03.2017.
 */
public class Bar implements Collider {
    private int left;
    private int bottom;
    private int width;
    private int height;

    public Bar() {

    }

    public Bar(int x1, int y1, int x2, int y2) {
        setFromCorners(x1, y1, x2, y2);
    }

    public Bar setFromCorners(int x1, int y1, int x2, int y2) {
        left = x1;
        bottom = y1;
        setWidth(x2 - x1);
        setHeight(y2 - y1);
        return this;
    }

    public static Bar fromPosAndSize(int x, int y, int width, int height) {
        final Bar bar = new Bar();
        bar.left = x;
        bar.bottom = y;
        bar.setWidth(width);
        bar.setHeight(height);
        return bar;
    }

    public Bar setLeftX(int x) {
        left = x;
        return this;
    }

    public int getLeftX() {
        return left;
    }

    public Bar setBottomY(int y) {
        bottom = y;
        return this;
    }

    public int getBottomY() {
        return bottom;
    }

    public Bar setWidth(int newWidth) {
        width = newWidth;
        if (width < 0) {
            left += width;
            width = -width;
        }
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Bar setHeight(int newHeight) {
        height = newHeight;
        if (height < 0) {
            bottom += height;
            height = -height;
        }
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Bar offset(int dx, int dy) {
        return new Bar(left + dx, bottom + dy, width, height);
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other instanceof Point) return isColliding((Point) other);
        if (other instanceof Bar) return isColliding((Bar) other);
        return false;
    }

    public boolean isColliding(Point point) {
        final boolean intersectsX = point.x >= left && point.x <= left + width;
        final boolean intersectsY = point.y >= bottom && point.y <= bottom + height;
        return intersectsX && intersectsY;
    }

    public boolean isColliding(Bar bar) {
        return left <= bar.left + bar.width
                && left + width >= bar.left
                && bottom + height >= bar.bottom
                && bottom <= bar.bottom + bar.height;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Bar)) return false;
        Bar bar = (Bar) other;
        return left == bar.left
                && bottom == bar.bottom
                && width == bar.width
                && height == bar.height;
    }
}
