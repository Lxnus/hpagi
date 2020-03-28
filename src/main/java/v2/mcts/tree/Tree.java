package v2.mcts.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import v2.mcts.environment.Environment;

public final class Tree<ValueT> {
  private final ValueT state;
  private final List<Tree<ValueT>> children;

  private double wins = 1.0D;
  private double visits = 1.0D;

  private Tree(ValueT state, List<Tree<ValueT>> children) {
    this.state = state;
    this.children = children;
  }

  private Optional<Tree<ValueT>> select() {
    return children.stream()
      .max(Comparator.comparing(this::computeConfidence));
  }

  private static final double CONSTANT = Math.sqrt(2);

  private double computeConfidence(Tree<ValueT> child) {
    double winVisitRatio = child.wins / child.visits;
    return winVisitRatio + CONSTANT * Math.sqrt(Math.log(this.visits) / child.visits);
  }

  private boolean isLeaf() {
    return children.isEmpty();
  }

  private void expand(Environment<ValueT> environment) {
    assert isLeaf();
    for (ValueT state : environment.getStates()) {
      addChild(Tree.createLeaf(state));
    }
  }

  private void addChild(Tree<ValueT> child) {
    children.add(child);
  }

  private Deque<Tree<ValueT>> visit(Environment<ValueT> environment) {
    return visit(environment, new LinkedList<>());
  }

  private Deque<Tree<ValueT>> visit(
    Environment<ValueT> environment,
    Deque<Tree<ValueT>> visited
  ) {
    boolean shouldStop = selectParentOrLastNode(visited)
      .map(with(node -> environment.apply(node.getState())))
      .filter(node -> node.isLeaf() || environment.isFinish())
      .isPresent();
    return shouldStop ? visited : visit(environment, visited);
  }

  private static <ValueT> Function<ValueT, ValueT> with(
    Consumer<ValueT> action
  ) {
    return value -> {
      action.accept(value);
      return value;
    };
  }

  private Optional<Tree<ValueT>> selectParentOrLastNode(
    Deque<Tree<ValueT>> visited
  ) {
    return visited.isEmpty()
      ? select()
      : visited.getLast().select();
  }

  private void update(double reward) {
    this.wins += reward;
    this.visits++;
  }

  public double rollOut(Environment<ValueT> environment) {
    maybeExpand(environment);
    var visited = visit(environment);
    if (shouldExpandLastVisitedNode(environment)) {
      visited.getLast().expand(environment);
    }
    updateNodes(visited, environment);
    environment.reset();
    return environment.getReward();
  }

  private void updateNodes(
    Iterable<Tree<ValueT>> nodes,
    Environment<ValueT> environment
  ) {
    double reward = environment.getReward();
    for (var node : nodes) {
      node.update(reward);
    }
  }

  private static final double INITIAL_REWARD = 1.0D;

  private boolean shouldExpandLastVisitedNode(
    Environment<ValueT> environment
  ) {
    double reward = environment.getReward();
    return !environment.isFinish() && reward != INITIAL_REWARD;
  }

  private void maybeExpand(Environment<ValueT> environment) {
    if (isLeaf()) {
      expand(environment);
    }
  }

  public ValueT getState() {
    return state;
  }

  @Override
  public String toString() {
    return "MCTS{" +
      "state=" + state +
      ", children=" + children +
      '}';
  }

  public static <ValueT> Tree<ValueT> createLeaf(ValueT value) {
    return new Tree<>(value, new ArrayList<>());
  }

  public static <ValueT> Tree<ValueT> createWithChildren(
    ValueT value,
    Tree<ValueT>... children
  ) {
    return new Tree<>(value, new ArrayList<>(Arrays.asList(children)));
  }
}
