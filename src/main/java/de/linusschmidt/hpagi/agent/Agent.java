package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Agent {

    private Printer printer;
    private CoreEngine coreEngine;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    public Agent() {
        this.printer = new Printer();
        this.bayesianNetworkBuilder = new BayesianNetworkBuilder();
    }

    public void setEnvironment(IEnvironment environment) {
        this.coreEngine = new CoreEngine(environment);
    }

    public void run() {
        long start = System.currentTimeMillis();
        double avgReward = this.coreEngine.solve();
        long end = System.currentTimeMillis();
        this.printer.printConsole(String.format("Duration: %s", (end - start) / 1000));
        this.printer.printConsole(String.format("MCTS: Avg.-Reward: %s", avgReward));
        List<double[]> trainingData = this.coreEngine.getMctsRootNode().trainingData(this.coreEngine.getEnvironment());
        for(double[] data : trainingData) {
            Utilities.printVector(data);
        }

        this.bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, trainingData);
        this.bayesianNetworkBuilder.generateBayesianNetwork();
    }
}
