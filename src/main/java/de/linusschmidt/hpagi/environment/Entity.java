package de.linusschmidt.hpagi.environment;

public class Entity {

    private int x;
    private int y;

    Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void update(int x, int y) {
        this.x += x;
        this.y += y;
    }

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }
}
