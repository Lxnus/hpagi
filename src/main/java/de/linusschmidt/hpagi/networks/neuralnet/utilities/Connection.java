package de.linusschmidt.hpagi.networks.neuralnet.utilities;

import de.linusschmidt.hpagi.networks.neuralnet.neurons.INeuron;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Connection {

	private double weight;

	private INeuron fromNeuron;
	private INeuron toNeuron;
	
	public Connection(double weight, INeuron fromNeuron, INeuron toNeuron) {
		this.weight = weight;
		this.fromNeuron = fromNeuron;
		this.toNeuron = toNeuron;
	}

	public void addWeight(double adjustment) {
		this.weight += adjustment;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public INeuron getFromNeuron() {
		return fromNeuron;
	}

	public INeuron getToNeuron() {
		return toNeuron;
	}
}
