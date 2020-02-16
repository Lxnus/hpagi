package de.linusschmidt.hpagi.cognitive;

import com.bayesserver.inference.InconsistentEvidenceException;
import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.network.Hopfield;
import de.linusschmidt.hpagi.utilities.MultithreadingUtilities;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class CognitiveAlgorithm {

    private Printer printer;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    private LinkedList<Hopfield> dynamicMemories;

    public CognitiveAlgorithm() {
        this.printer = new Printer();

        this.dynamicMemories = new LinkedList<>();
    }

    public void setData(List<double[]> data) throws InterruptedException {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        final List<List<double[]>> partitions = MultithreadingUtilities.partition(threads, data);
        final List<Callable<Void>> workers = this.createWorkers(partitions);
        executorService.invokeAll(workers);
        executorService.shutdown();
    }

    public void cognitivePrediction(double[] X) throws InconsistentEvidenceException {
        this.bayesianNetworkBuilder.predict(X, 0);
        this.bayesianNetworkBuilder.predict(X, 1);
    }

    public void generateBayesianNetwork(String[] nodeDescription, String[] dataDescription, List<double[]> data) {
        this.bayesianNetworkBuilder = new BayesianNetworkBuilder();
        this.bayesianNetworkBuilder.setData(nodeDescription, dataDescription, data);
        this.bayesianNetworkBuilder.generateBayesianNetwork();
    }

    private synchronized List<Callable<Void>> createWorkers(final List<List<double[]>> partitions) {
        final List<Callable<Void>> workers = new CopyOnWriteArrayList<>();
        for (final List<double[]> partition : partitions) {
            Callable<Void> worker = this.worker(partition);
            workers.add(worker);
        }
        return workers;
    }

    private Callable<Void> worker(final List<double[]> data) {
        return () -> {
            synchronized (data) {
                for(final double[] vec : data) {
                    final Hopfield dynamicMemory = new Hopfield(vec.length);
                    dynamicMemory.addData(vec);
                    dynamicMemory.addData(vec);
                    dynamicMemory.train();
                    addDynamicMemory(dynamicMemory);
                }
            }
            return null;
        };
    }

    private synchronized void addDynamicMemory(final Hopfield hopfield) {
        this.dynamicMemories.add(hopfield);
    }

    public void print() {
        this.printer.printConsole(String.format("Dyn.-Memories: %s", this.dynamicMemories.size()));
    }
}
