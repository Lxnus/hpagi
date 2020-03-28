package v2.mcts.environment;

import java.util.List;


public interface Environment<T> {

    List<T> getStates();

    void apply(T state);

    double getReward();

    boolean isFinish();

    Position getState();

    void reset();
}
