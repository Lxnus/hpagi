package v2.mcts.tree;

import v2.mcts.environment.IEnvironment;

import java.util.*;

/**
 * @author Linus Schmidt
 */
public final class MCTS<T extends Object> {

    private T state;

    private double wins = 1.0D;
    private double visits = 1.0D;
    private double constant = Math.sqrt(2);

    private List<MCTS<T>> children;

    public MCTS(T state) {
        this.state = state;

        this.children = new ArrayList<>();
    }

    private MCTS<T> select() {
        Optional<MCTS<T>> optional = this.children.stream().max(Comparator.comparing(this::computeUCTValue));
        return optional.orElse(null);
    }

    private double computeUCTValue(MCTS<T> child) {
        return child.wins / child.visits + this.constant * Math.sqrt(Math.log(this.visits) / child.visits);
    }

    private boolean isLeaf() {
        return this.children.size() == 0;
    }

    private void expand(IEnvironment<T> environment) {
        assert this.isLeaf();
        environment.getStates().forEach(state -> this.addChild(new MCTS<>(state)));
    }

    private void addChild(MCTS<T> child) {
        children.add(child);
    }

    private LinkedList<MCTS<T>> walkThrough(IEnvironment<T> environment, LinkedList<MCTS<T>> visited) {
        MCTS<T> selected = visited.size() == 0 ? this.select() : visited.getLast().select();
        visited.add(selected);
        environment.apply(selected.getState());
        return selected.isLeaf() || environment.isFinish()
            ? visited
            : this.walkThrough(environment, visited);
    }

    private void update(double reward) {
        this.wins += reward;
        this.visits++;
    }

    public double rollOut(IEnvironment<T> environment) {
        if(this.isLeaf()) {
            this.expand(environment);
        }
        LinkedList<MCTS<T>> visited = this.walkThrough(environment, new LinkedList<>());
        if(!environment.isFinish() && environment.getReward() != 1.0D) {
            visited.getLast().expand(environment);
        }
        final double reward = environment.getReward();
        System.out.println(reward);
        visited.forEach(node -> node.update(reward));
        environment.reset();
        return reward;
    }

    public T getState() {
        return state;
    }

    public double getWins() {
        return wins;
    }

    public double getVisits() {
        return visits;
    }

    public double getConstant() {
        return constant;
    }

    public List<MCTS<T>> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "MCTS{" +
            "state=" + state +
            ", wins=" + wins +
            ", visits=" + visits +
            ", constant=" + constant +
            ", children=" + children +
            '}';
    }
}
