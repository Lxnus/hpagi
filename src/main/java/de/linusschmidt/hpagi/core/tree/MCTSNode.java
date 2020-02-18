package de.linusschmidt.hpagi.core.tree;

import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class MCTSNode {

    private double s = -1;
    private double w = 0.0D;
    private double v = 1.0D;
    private double c = Math.sqrt(2);

    private List<MCTSNode> children;

    public MCTSNode() {
        this.children = new ArrayList<>();
    }

    public MCTSNode(double s) {
        this.s = s;

        this.children = new ArrayList<>();
    }

    public void expand(LinkedList<Double> data) {
        if(data.size() > 0) {
            MCTSNode child = new MCTSNode();
            child.s = data.getFirst();
            child.w = 1;
            child.v = 1;
            data.removeFirst();
            System.out.println(data.size());
            this.addNode(child);
            child.expand(data);
        }
    }

    private MCTSNode select() {
        MCTSNode best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for(MCTSNode child : this.children) {
            assert child.v > 0;
            double uct = (child.v == 0 ? 0 : child.w / child.v + this.c * Math.sqrt(Math.log(this.v + 1) / child.v));
            if(uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        return best;
    }

    private boolean isLeaf() {
        return this.children.size() == 0;
    }

    private void expand(IEnvironment environment) {
        if(this.isLeaf()) {
            for(int i = 0; i < environment.possibleActions().length; i++) {
                double s = environment.possibleActions()[i];
                MCTSNode node = new MCTSNode(s);
                this.addNode(node);
            }
        }
    }

    private void update(double reward) {
        this.w += reward;
        ++this.v;
    }

    public double rollOut(IEnvironment environment) {
        List<MCTSNode> visited = new LinkedList<>();
        MCTSNode current = this;
        boolean isFinish = environment.isFinish();
        while(!current.isLeaf() && !isFinish) {
            current = current.select();
            visited.add(current);
            environment.apply(current.s);
            isFinish = environment.isFinish();
        }
        if(!isFinish) {
            current.expand(environment);
            current = current.select(); //this.selection(environment);
            environment.apply(current.s);
        }
        for(MCTSNode node : visited) {
            node.update(environment.getReward());
        }
        System.out.println(environment.getReward());
        return environment.getReward();
    }

    public LinkedList<double[]> trainingData(IEnvironment environment) {
        LinkedList<double[]> trainingData = new LinkedList<>();
        MCTSNode current = this;
        boolean isFinish = environment.isFinish();
        while(!current.isLeaf() && !isFinish) {
            current = current.select();
            environment.apply(current.s);
            isFinish = environment.isFinish();
            double[] data = new double[environment.possibleActions().length];
            for (int i = 0; i < data.length; i++) {
                if (i == current.s) {
                    data[i] = 1.0D;
                }
            }
            trainingData.add(data);
        }
        return trainingData;
    }

    private MCTSNode selection(IEnvironment environment) {
        MCTSNode current = this;
        boolean isFinish = environment.isFinish();
        while(!current.isLeaf() && !isFinish) {
            current = current.select();
            environment.apply(current.s);
            isFinish = environment.isFinish();
        }
        return current;
    }

    public int getNodes() {
        int nodes = this.children.size();
        for(MCTSNode mctsNode : this.children) {
            nodes += mctsNode.getNodes();
        }
        return nodes;
    }

    private void addNode(MCTSNode node) {
        this.children.add(node);
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public double getV() {
        return v;
    }

    public double getW() {
        return w;
    }

    public double getS() {
        return s;
    }
}
