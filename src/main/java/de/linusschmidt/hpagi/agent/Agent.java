package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.LinkedList;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Agent {

    private CoreEngine coreEngine;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    public Agent() {
        this.bayesianNetworkBuilder = new BayesianNetworkBuilder();
    }

    public void setEnvironment(IEnvironment environment) {
        this.coreEngine = new CoreEngine(environment);
    }

    public void run() {
        this.coreEngine.solve(0.95);
        LinkedList<double[]> trainingData = this.coreEngine.getTrainingData();
        if(trainingData != null) {
            for(double[] data : trainingData) {
                Utilities.printVector(data);
            }
            this.bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, trainingData);
            this.bayesianNetworkBuilder.generateBayesianNetwork();
        }
    }
}
