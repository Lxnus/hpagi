package v2.mcts.ua;

import java.util.Arrays;
import java.util.stream.IntStream;

public class UniversalAgent {

    private Double[][] table;

    public UniversalAgent() {}

    private void buildTable(int states, int actions) {
        this.table = new Double[actions][states];
        this.table = IntStream.range(0, actions)
            .mapToObj(x -> IntStream.range(0, states)
                .mapToObj(y -> Math.random())
                .toArray(Double[]::new))
            .toArray(Double[][]::new);
    }

    private void run() {
        buildTable(4, 3);

        this.table[2][2] = 1.0D;

        int action = 0;
        int state = 1;
        for(int i = 0; i < 1; i++) {
            System.out.println(max(this.table[action]));
        }
    }

    private double max(Double[] array) {
        return Arrays.stream(array).max((o1, o2) -> {
            if(o1 > o2) {
                return 1;
            } else if(o1 < o2) {
                return -1;
            }
            return 0;
        }).get();
    }

    private void update(int oldState, int newState, int action, double reward) {
        double q = reward + 0.95 * this.table[newState][action];
        this.table[oldState][action] = q;
    }

    public static void main(String[] args) {
        UniversalAgent universalAgent = new UniversalAgent();
        universalAgent.run();
    }
}
