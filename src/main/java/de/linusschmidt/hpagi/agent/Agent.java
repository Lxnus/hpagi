package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.core.neuralnet.neuralnetwork.NeuralNetwork;
import de.linusschmidt.hpagi.core.neuralnet.utilities.Functions;
import de.linusschmidt.hpagi.draw.TreeView;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.List;

public class Agent {

    private Printer printer;
    private CoreEngine coreEngine;
    private NeuralNetwork neuralNetwork;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    public Agent() {
        this.printer = new Printer();
        this.neuralNetwork = new NeuralNetwork();
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

        this.printer.printConsole("Build bayesian network...");
        this.bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, trainingData);
        this.bayesianNetworkBuilder.generateBayesianNetwork();

        this.printer.printConsole("Build neural network...");
        this.neuralNetwork.setInputNeurons(4);
        this.neuralNetwork.setHiddenNeurons(8);
        this.neuralNetwork.setOutputNeurons(4);
        this.neuralNetwork.createNetwork();
        this.neuralNetwork.hiddenFunction(Functions.TANH);
        this.neuralNetwork.outputFunction(Functions.SIGMOID);
        for(int i = 0; i < 10000; i++) {
            for(int j = 1; j < trainingData.size(); j++) {
                for(int k = 0; k < trainingData.get(j - 1).length; k++) {
                    this.neuralNetwork.getInputNeurons().get(k).setValue(trainingData.get(j - 1)[k]);
                }
                this.neuralNetwork.train(0.9D, trainingData.get(j), 1);
            }
        }
        this.neuralNetwork.printOutputs();
    }
}
