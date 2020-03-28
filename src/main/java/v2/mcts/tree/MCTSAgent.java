package v2.mcts.tree;

import v2.mcts.environment.MazeEnvironment;
import v2.mcts.environment.Environment;
import smile.plot.Line;
import smile.plot.LinePlot;
import smile.plot.PlotCanvas;

import javax.swing.*;
import java.awt.*;

public final class MCTSAgent {

    private Tree<Object> rootNode;

    public MCTSAgent() {
        this.rootNode = new Tree<>(null);
    }

    public void run(Environment environment) {
        int size = 1000;
        double[][] points = new double[size][2];
        for(int i = 0; i < size; i++) {
            double reward = rootNode.rollOut((Environment<Object>) environment);
            points[i] = new double[] {
                i, reward
            };
        }

        PlotCanvas plotCanvas = LinePlot.plot(points, Line.Style.DOT_DASH, Color.RED);
        JFrame frame = new JFrame();
        frame.add(plotCanvas);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MazeEnvironment environment = new MazeEnvironment();
        MCTSAgent agent = new MCTSAgent();
        agent.run(environment);
    }
}
