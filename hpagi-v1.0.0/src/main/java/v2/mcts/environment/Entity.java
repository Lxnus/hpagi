package v2.mcts.environment;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
class Entity {

    private Position position;

    Entity(int x, int y) {
        this.position = new Position(x, y);
    }

    void update(int x, int y) {
        this.position.update(x, y);
    }

    public Position getPosition() {
        return position;
    }
}
