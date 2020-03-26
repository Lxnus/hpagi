package de.linusschmidt.hpagi.cognitive;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.environment.Environment;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.tree.MCTSExecutor;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.LinkedList;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class CognitiveAlgorithm {

    private IEnvironment environment;

    private Printer printer;
    private MCTSExecutor mctsExecutor;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    public CognitiveAlgorithm(IEnvironment environment) {
        this.environment = environment;

        this.printer = new Printer();
        this.mctsExecutor = new MCTSExecutor(environment);
        this.bayesianNetworkBuilder = new BayesianNetworkBuilder();
    }

    private void run() {
        LinkedList<Double> actions = this.mctsExecutor.solve(0.9);
        this.printer.printConsole(String.format("Actions (Nodes: %s):", actions.size()));
        this.printer.printConsole(actions.toString());
    }

    private void connect() {
        this.run();

        int actions = this.environment.possibleActions().length;

        this.printer.printConsole(String.format("Actions (length): %s", actions));
    }

    public static void main(String[] args) {
        IEnvironment environment = new Environment();
        CognitiveAlgorithm cognitiveAlgorithm = new CognitiveAlgorithm(environment);
        cognitiveAlgorithm.connect();
    }
}
