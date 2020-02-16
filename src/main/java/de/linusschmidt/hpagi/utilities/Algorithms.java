package de.linusschmidt.hpagi.utilities;

import java.util.*;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Algorithms {

    public Graph.Node bfs(Graph graph, double s) {
        Queue<Graph.Node> queue = new LinkedList<>();
        Set<Graph.Node> visited = new HashSet<>();
        queue.add(graph.getNodes().get(0));
        while(!queue.isEmpty()) {
            Graph.Node current = queue.poll();
            if(current.getState() == s) {
                return current;
            }
            visited.add(current);
            for(Graph.Node neighbour : current.getNeighbours()) {
                if(!visited.contains(neighbour)) {
                    queue.add(neighbour);
                }
            }
        }
        return null;
    }

    public LinkedList<Double> markovChain(double[][] transition) {
        LinkedList<Double> chain = new LinkedList<>();
        int n = transition.length;
        int state = n - 1;
        while(state > 0) {
            chain.add((double) state);
            double random = Math.random();
            double sum = 0.0D;
            for(int i = 0; i < n; i++) {
                sum += transition[state][i];
                if(sum >= random) {
                    state = i;
                    break;
                }
            }
        }
        return chain;
    }
}
