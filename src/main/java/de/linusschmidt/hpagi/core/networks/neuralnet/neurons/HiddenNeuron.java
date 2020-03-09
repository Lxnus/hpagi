package de.linusschmidt.hpagi.core.networks.neuralnet.neurons;

import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Connection;
import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Functions;
import de.linusschmidt.hpagi.utilities.MathUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class HiddenNeuron implements INeuron {

    private double biasValue = 0.0D;
	private double neuronValue = 0.0D;
	
	private Functions function = null;
	
	private List<Connection> connections = new ArrayList<>();
	
	@Override
	public void setValue(double value) {
		this.neuronValue = value;
	}

	@Override
	public double getValue() {
		this.reset();
		for(Connection connection : this.connections) {
			this.neuronValue += connection.getWeight() * connection.getFromNeuron().getValue() + this.biasValue;
		}
		return function();
	}

	@Override
	public void reset() {
		this.neuronValue = 0.0D;
	}

	@Override
	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}

	private double function() {
		double value = 0.0D;
		switch (this.function) {
		case SIGMOID:
			value = MathUtilities.sigmoid(this.neuronValue);
			break;
		case IDENTITY:
			value = MathUtilities.identity(this.neuronValue);
			break;
		case TANH:
			value = MathUtilities.tanh(this.neuronValue);
			break;
		default:
			break;
		}
		return value;
	}

	public void setBiasValue(double biasValue) {
		this.biasValue = biasValue;
	}

	public void setFunction(Functions function) {
		this.function = function;
	}

	public List<Connection> getConnections() {
		return connections;
	}
}
