package v2.mcts.environment;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Wall {

    private int x;
    private int y;
    private int visible;

    private Wall(int x, int y, boolean visible) {
        this.x = x;
        this.y = y;
        this.visible = !visible ? 0 : 1;
    }

    static Wall loadWall(String line) {
        String[] split = line.split(";");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int visible = Integer.parseInt(split[2]);
        return new Wall(x, y, (visible != 0));
    }

    @Override
    public String toString() {
        return x + ";" + y + ";" + visible;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    boolean isVisible() {
        return visible != 0;
    }
}
