package v2.mcts.environment;

import java.util.List;

/**
 * @author Linus Schmidt
 */
public interface IEnvironment<T extends Object> {

    List<T> getStates();

    void apply(T state);

    double getReward();

    boolean isFinish();

    Position getState();

    void reset();
}
