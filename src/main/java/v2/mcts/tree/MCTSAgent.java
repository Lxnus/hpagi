package v2.mcts.tree;

import v2.mcts.environment.Environment;
import v2.mcts.environment.IEnvironment;
import smile.plot.Line;
import smile.plot.LinePlot;
import smile.plot.PlotCanvas;

import javax.swing.*;
import java.awt.*;

public final class MCTSAgent {

    private MCTS<Object> rootNode;

    public MCTSAgent() {
        this.rootNode = new MCTS<>(null);
    }

    public void run(IEnvironment environment) {
        double[][] points = new double[1000][2];
        for(int i = 0; i < 1000; i++) {
            double reward = rootNode.rollOut((IEnvironment<Object>) environment);
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
        Environment environment = new Environment();
        MCTSAgent agent = new MCTSAgent();
        agent.run(environment);
    }
}
