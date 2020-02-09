package de.linusschmidt.hpagi.core.neuralnet.neurons;

import de.linusschmidt.hpagi.core.neuralnet.utilities.Connection;

public class InputNeuron implements INeuron {

    /**
     * Author: Linus Schmidt!
     * All rights reserved!
     */

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
