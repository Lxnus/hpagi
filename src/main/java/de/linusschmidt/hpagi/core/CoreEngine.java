package de.linusschmidt.hpagi.core;

import de.linusschmidt.hpagi.core.tree.MCTSNode;
import de.linusschmidt.hpagi.environment.IEnvironment;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class CoreEngine {

    private MCTSNode mctsRootNode;
    private IEnvironment environment;

    public CoreEngine(IEnvironment environment) {
        this.environment = environment;

        this.mctsRootNode = new MCTSNode(-1);
    }

    private double run() {
        int counter = 0;
        double average = 0.0D;
        double reward = this.mctsRootNode.rollOut(this.environment);
        if(reward >= 0) {
            average = reward;
            counter++;
        }
        this.environment.reset();

        return (average / counter);
    }

    public double solve() {
        double avgReward = 0.0D;
        while(avgReward < 0.9) {
            avgReward = this.run(); //this.run((int) Math.round(Math.random() * 1000));
        }
        return avgReward;
    }

    public MCTSNode getMctsRootNode() {
        return mctsRootNode;
    }

    public IEnvironment getEnvironment() {
        return environment;
    }
}
