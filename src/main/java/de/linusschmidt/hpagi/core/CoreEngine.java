package de.linusschmidt.hpagi.core;

import de.linusschmidt.hpagi.core.tree.MCTSNode;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;
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
    private MCTSNode rootNode;
    private IEnvironment environment;

    public CoreEngine(IEnvironment environment) {
        this.environment = environment;

        this.printer = new Printer();
        this.rootNode = new MCTSNode(-1);
    }

    private double run() {
        double reward = this.rootNode.rollOut(this.environment);
        this.environment.reset();
        if(reward == 1 && !this.compressed) {
            this.compress();
        }
        return reward;
    }

    private void compress() {
        this.compressed = true;
        LinkedList<double[]> data = this.rootNode.trainingData(this.environment);
        this.environment.reset();
        LinkedList<Double> processed = new LinkedList<>();
        for(double[] vector : data) {
            for (int k = 0; k < vector.length; k++) {
                if (vector[k] == 1) {
                    processed.add((double) k);
                    break;
                }
            }
        }
        int nodes = this.rootNode.getNodes();
        MCTSNode compressedTree = new MCTSNode();
        compressedTree.expand(processed);
        if(compressedTree.getNodes() < nodes) {
            this.printer.printConsole(String.format("Nodes-before: %s", this.rootNode.getNodes()));
            this.rootNode = compressedTree;
            this.printer.printConsole(String.format("Nodes-after: %s", this.rootNode.getNodes()));
        }
        this.environment.reset();
    }

    public LinkedList<Double> solve(double accuracy) {
        double reward = 0.0D;
        LinkedList<Double> data = new LinkedList<>();
        while(reward < accuracy) {
            reward = this.run();
            data.add(reward);
        }
        return data;
    }

    public LinkedList<double[]> getTrainingData() {
        return this.rootNode.trainingData(this.environment);
    }

    public void testMCTS() {
        this.printer.printConsole("Testing monte carlo...");
        double[][] data = new double[10][];
        for(int i = 0; i < data.length; i++) {
            long start = System.currentTimeMillis();
            LinkedList<Double> list = this.solve(0.95);
            long stop = System.currentTimeMillis();
            this.printer.printConsole(String.format("Computation time: %s", (stop - start)));
            double[] vec = new double[2];
            double avg = 0.0D;
            for (Double aDouble : list) {
                avg += aDouble;
            }
            vec[1] = avg / (double) list.size();
            vec[0] = i;
            data[i] = vec;
        }

        PlotCanvas plotCanvas = LinePlot.plot(data, Line.Style.DOT_DASH, Color.RED);
        JFrame frame = new JFrame();
        frame.add(plotCanvas);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
