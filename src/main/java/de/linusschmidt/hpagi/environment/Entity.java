package de.linusschmidt.hpagi.environment;

public class Entity {

    private int x;
    private int y;

    Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(int x, int y) {
        this.x += x;
        this.y += y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
}
