package de.linusschmidt.hpagi.core;

import de.linusschmidt.hpagi.core.tree.MCTSNode;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class CoreEngine {

    private Printer printer;
    private MCTSNode mctsRootNode;
    private IEnvironment environment;

    public CoreEngine(IEnvironment environment) {
        this.environment = environment;

        this.printer = new Printer();
        this.mctsRootNode = new MCTSNode(-1);
    }

    private double run(int maxIter) {
        int counter = 0;
        double average = 0.0D;
        for(int i = 0; i < maxIter; i++) {
            double reward = this.mctsRootNode.rollOut(this.environment);
            if(reward >= 0) {
                average += reward;
                counter++;
            }
            this.environment.reset();
        }
        return (average / counter);
    }

    public void solve() {
        double avgReward = 0.0D;
        while(avgReward < 0.9) {
            avgReward = this.run((int) Math.round(Math.random() * 1000));
        }
    }

    public MCTSNode getMctsRootNode() {
        return mctsRootNode;
    }

    public IEnvironment getEnvironment() {
        return environment;
    }
}
