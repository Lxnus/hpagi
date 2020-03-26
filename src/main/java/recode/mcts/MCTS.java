package recode.mcts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class MCTS<ObjectT> {

    private ObjectT state;

    private double wins = 0.0D;
    private double visits = 1.0D;
    private double constant = Math.sqrt(2);

    private List<MCTS<ObjectT>> children;

    public MCTS(ObjectT state) {
        this.state = state;

        children = new ArrayList<>();
    }

    private MCTS<ObjectT> select() {
        Optional<MCTS<ObjectT>> optional = Optional.of(children.stream()
            .filter(
                child -> (child.visits > 0))
            .max(
                Comparator.comparing(this::computeUCTValue))
            .get());
        return optional.orElse(this);
    }

    public double computeUCTValue(MCTS<ObjectT> child) {
        return (child.visits == 0
            ? 0
            : child.wins / child.visits + constant * Math.sqrt(Math.log(visits) / child.visits));
    }

    public void addChild(MCTS<ObjectT> child) {
        children.add(child);
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

    public static void main(String[] args) {
        MCTS<Double> node = new MCTS<>(-1.0D);

        MCTS<Double> child1 = new MCTS<>(1.0D);
        child1.visits = 0;
        MCTS<Double> child2 = new MCTS<>(3.0D);
        child2.visits = 2;
        MCTS<Double> child3 = new MCTS<>(2.0D);

        node.addChild(child1);
        node.addChild(child2);
        node.addChild(child3);

        System.out.println(node.select());
    }
}
