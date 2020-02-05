package de.linusschmidt.hpagi.utilities;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    public static class Node {

        private double state;

        private List<Node> neighbours;

        public Node(double state) {
            this.state = state;

            this.neighbours = new ArrayList<>();
        }

        public void addNeighbour(Node node) {
            this.neighbours.add(node);
        }

        public double getState() {
            return state;
        }

        public List<Node> getNeighbours() {
            return neighbours;
        }
    }

    private List<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
