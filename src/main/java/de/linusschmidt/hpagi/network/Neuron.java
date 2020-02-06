package de.linusschmidt.hpagi.network;

import java.util.ArrayList;
import java.util.List;

public class Neuron {

    private byte value;

    private List<Connection> connections;

    public Neuron(byte value) {
        this.value = value;

        this.connections = new ArrayList<>();
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public byte getValue() {
        return value;
    }
}
