package de.linusschmidt.hpagi.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class MultithreadingUtilities {

    public static List<List<double[]>> partition(int threads, List<double[]> data) {
        List<List<double[]>> partition = new ArrayList<>();
        int elementsPerThread = (data.size() >= threads) ? (data.size() / threads) : 1;
        int effectiveParallelism = Math.min(data.size(), threads);
        for(int iParallelism = 0; iParallelism < effectiveParallelism; iParallelism++) {
            int from = iParallelism * elementsPerThread;
            int to = iParallelism == (effectiveParallelism - 1) ? (data.size()) : ((iParallelism + 1) * elementsPerThread);
            partition.add(data.subList(from, to));
        }
        return partition;
    }
}
