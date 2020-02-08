package de.linusschmidt.hpagi.cognitive;

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

    private LinkedList<Hopfield> dynamicMemory;

    public CognitiveAlgorithm() {
        this.printer = new Printer();

        this.dynamicMemory = new LinkedList<>();
    }

    public void setData(List<double[]> data) throws InterruptedException {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        final List<List<double[]>> partitions = MultithreadingUtilities.partition(threads, data);
        final List<Callable<Void>> workers = this.createWorkers(partitions);
        executorService.invokeAll(workers);
        executorService.shutdown();
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
        this.dynamicMemory.add(hopfield);
    }

    public void print() {
        this.printer.printConsole("Dyn.-Memories: " + this.dynamicMemory.size());
    }
}
