package common;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(x, y);
    }

    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Position withX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position withY(int y) {
        this.y = y;
        return this;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}