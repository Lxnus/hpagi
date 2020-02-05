package de.linusschmidt.hpagi.utilities;

import java.util.*;

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
}
