package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.draw.TreeView;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.List;

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
        this.coreEngine.solve();
        List<double[]> trainingData = this.coreEngine.getMctsRootNode().trainingData(this.coreEngine.getEnvironment());
        for(double[] data : trainingData) {
            Utilities.printVector(data);
        }
        TreeView treeView = new TreeView(this.coreEngine.getMctsRootNode());
        treeView.showTree("Test");

        this.bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, trainingData);
        this.bayesianNetworkBuilder.generateBayesianNetwork();
    }
}
