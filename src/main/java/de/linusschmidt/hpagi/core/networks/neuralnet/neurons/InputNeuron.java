package de.linusschmidt.hpagi.core.networks.neuralnet.neurons;

import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Connection;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class InputNeuron implements INeuron {

	private double neuronValue = 0.0D;
	
	@Override
	public void setValue(double value) {
		this.neuronValue = value;
	}

	@Override
	public double getValue() {
		return this.neuronValue;
	}

	@Override
	public void reset() {
		this.neuronValue = 0.0D;
	}
	
	@Override
	public void addConnection(Connection connection) {}

}
