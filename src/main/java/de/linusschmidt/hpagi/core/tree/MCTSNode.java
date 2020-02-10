package de.linusschmidt.hpagi.core.tree;

import de.linusschmidt.hpagi.environment.IEnvironment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MCTSNode {

    private double s;
    private double w = 0.0D;
    private double v = 0.0D;
    private double c = Math.sqrt(2);

    private List<MCTSNode> children;

    public MCTSNode(double s) {
        this.s = s;

        this.children = new ArrayList<>();
    }

    private MCTSNode select(IEnvironment environment) {
        MCTSNode best = null;
        double bestValue = Double.MIN_VALUE;
        for(MCTSNode child : this.children) {
            double buffer = child.w / child.v + child.c * Math.sqrt(Math.log(this.v + 1) / child.v);
            double epsilon = 1e-6;
            double uct = Double.isNaN(buffer) ? Math.random() * epsilon : buffer * environment.requestReward(child.s);
            if(uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        if(best == null || Double.isNaN(bestValue)) {
            best = this.children.get((int) (Math.random() * this.children.size()));
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
        this.v += 1;
    }

    public double rollOut(IEnvironment environment) {
        List<MCTSNode> visited = new LinkedList<>();
        MCTSNode current = this;
        boolean isFinish = environment.isFinish();
        while(!current.isLeaf() && !isFinish) {
            current = current.select(environment);
            visited.add(current);
            environment.apply(current.s);
            isFinish = environment.isFinish();
        }
        if(!isFinish) {
            current.expand(environment);
            current = this.selection(environment);
            environment.apply(current.s);
        }
        for(MCTSNode node : visited) {
            node.update(environment.getReward());
        }
        return environment.getReward();
    }

    public LinkedList<double[]> trainingData(IEnvironment environment) {
        LinkedList<double[]> trainingData = new LinkedList<>();
        MCTSNode current = this;
        boolean isFinish = environment.isFinish();
        while(!current.isLeaf() && !isFinish) {
            current = current.select(environment);
            environment.apply(current.s);
            isFinish = environment.isFinish();
            double[] data = new double[environment.possibleActions().length];
            for(int i = 0; i < data.length; i++) {
                if(i == current.s) {
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
            current = current.select(environment);
            environment.apply(current.s);
            isFinish = environment.isFinish();
        }
        return current;
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
}
