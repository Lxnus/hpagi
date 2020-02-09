package de.linusschmidt.hpagi.core;

import de.linusschmidt.hpagi.core.tree.MCTSNode;
import de.linusschmidt.hpagi.environment.IEnvironment;

public class CoreEngine {

    private MCTSNode mctsRootNode;
    private IEnvironment environment;

    public CoreEngine(IEnvironment environment) {
        this.environment = environment;

        this.mctsRootNode = new MCTSNode(-1);
    }

    public double run(int maxIter) {
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
}
