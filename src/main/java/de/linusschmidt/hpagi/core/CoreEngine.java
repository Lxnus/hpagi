package de.linusschmidt.hpagi.core;

import de.linusschmidt.hpagi.core.tree.MCTSNode;
import de.linusschmidt.hpagi.draw.TreeView;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;
import smile.plot.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class CoreEngine {

    private boolean compressed = false;

    private Printer printer;
    private MCTSNode mctsRootNode;
    private IEnvironment environment;

    public CoreEngine(IEnvironment environment) {
        this.environment = environment;

        this.printer = new Printer();
        this.mctsRootNode = new MCTSNode(-1);
    }

    private double run() {
        double reward = this.mctsRootNode.rollOut(this.environment);
        this.environment.reset();
        if(reward == 1 && !this.compressed) {
            new TreeView(this.mctsRootNode).showTree("Before");
            this.compress();
            new TreeView(this.mctsRootNode).showTree("After");
        }
        return reward;
    }

    private void compress() {
        this.compressed = true;
        LinkedList<double[]> data = this.mctsRootNode.trainingData(this.environment);
        this.environment.reset();
        LinkedList<Double> processed = new LinkedList<>();
        for(double[] vector : data) {
            Utilities.printVector(vector);
            for (int k = 0; k < vector.length; k++) {
                if (vector[k] == 1) {
                    processed.add((double) k);
                    break;
                }
            }
        }
        int nodes = this.mctsRootNode.getNodes();
        MCTSNode compressedTree = new MCTSNode();
        compressedTree.expand(processed);
        if(compressedTree.getNodes() < nodes) {
            this.printer.printConsole(String.format("Nodes-before: %s", this.mctsRootNode.getNodes()));
            this.mctsRootNode = compressedTree;
            this.printer.printConsole(String.format("Nodes-after: %s", this.mctsRootNode.getNodes()));
        }
        this.environment.reset();
    }

    public LinkedList<Double> solve(double accuracy) {
        double avgReward = 0.0D;
        LinkedList<Double> data = new LinkedList<>();
        while(avgReward < accuracy) {
            avgReward = this.run();
            data.add(avgReward);
        }
        return data;
    }

    public void testMCTS() {
        this.printer.printConsole("Testing monte carlo...");
        double[][] data = new double[100][];
        for(int i = 0; i < 100; i++) {
            this.printer.printConsole("****");
            long start = System.currentTimeMillis();
            LinkedList<Double> list = this.solve(0.95);
            long end = System.currentTimeMillis();
            double[] vec = new double[2];
            double avg = 0.0D;
            for (Double aDouble : list) {
                avg += aDouble;
            }
            vec[1] = avg / (double) list.size();
            vec[0] = i;
            data[i] = vec;
            this.printer.printConsole(String.format("Time: %s", (end - start)));
        }

        PlotCanvas plotCanvas = LinePlot.plot(data, Line.Style.DOT_DASH, Color.RED);
        JFrame frame = new JFrame();
        frame.add(plotCanvas);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

    public MCTSNode getMctsRootNode() {
        return mctsRootNode;
    }

    public IEnvironment getEnvironment() {
        return environment;
    }
}
