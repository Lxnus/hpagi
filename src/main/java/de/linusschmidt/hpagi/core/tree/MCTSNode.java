package de.linusschmidt.hpagi.core.tree;

import de.linusschmidt.hpagi.environment.IEnvironment;

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

    private LinkedList<MCTSNode> successWay;

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
        LinkedList<MCTSNode> visited = new LinkedList<>();
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
            current = current.select();
            environment.apply(current.s);
            visited.add(current);
        }
        for(MCTSNode node : visited) {
            node.update(environment.getReward());
        }
        this.successWay = environment.getReward() == 1 ? visited : null;
        return environment.getReward();
    }

    public LinkedList<double[]> trainingData(IEnvironment environment) {
        if(this.successWay != null) {
            LinkedList<double[]> trainingData = new LinkedList<>();
            for (MCTSNode current : this.successWay) {
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
        return null;
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

    public double getS() {
        return s;
    }
}
