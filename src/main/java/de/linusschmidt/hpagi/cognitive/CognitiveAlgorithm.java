package de.linusschmidt.hpagi.cognitive;

import com.bayesserver.data.DataRowCollection;
import com.bayesserver.data.DataTable;
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

public class CognitiveAlgorithm {

    private Printer printer;

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

        this.generateBayesianNetwork(data);
    }

    public void cognitivePrediction(double[] X) {

    }

    private void generateBayesianNetwork(List<double[]> data) {
        BayesianNetworkBuilder bayesianNetworkBuilder = new BayesianNetworkBuilder() {
            @Override
            public void generateDataRowCollection(DataTable dataTable, List<String[]> data) {
                DataRowCollection dataRowCollection = dataTable.getRows();
                for (String[] vec : data) {
                    dataRowCollection.add(vec[0], vec[1], vec[2], vec[3], vec[4], vec[5], vec[6], vec[7], vec[8], vec[9]);
                }
            }
        };
        bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"}, data);
        bayesianNetworkBuilder.generateBayesianNetwork();
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
        this.printer.printConsole("Dyn.-Memories: " + this.dynamicMemories.size());
    }
}
