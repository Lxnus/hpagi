package de.linusschmidt.hpagi.core.networks.neuralnet.neuralnetwork;

import de.linusschmidt.hpagi.core.networks.neuralnet.neurons.HiddenNeuron;
import de.linusschmidt.hpagi.core.networks.neuralnet.neurons.InputNeuron;
import de.linusschmidt.hpagi.core.networks.neuralnet.neurons.OutputNeuron;
import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Connection;
import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Functions;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class NeuralNetwork {

	public Printer printer;

	private List<InputNeuron> inputNeurons = new ArrayList<>();
	private List<HiddenNeuron> hiddenNeurons = new ArrayList<>();
	private List<OutputNeuron> outputNeurons = new ArrayList<>();

    public NeuralNetwork() {
    	this.printer = new Printer();
	}

	public NeuralNetwork initWeights(double value) {
		for (HiddenNeuron hiddenNeuron : this.hiddenNeurons) {
			for (int j = 0; j < hiddenNeuron.getConnections().size(); j++) {
				hiddenNeuron.getConnections().get(j).setWeight(value);
			}
		}
		for (OutputNeuron outputNeuron : this.outputNeurons) {
			for (int j = 0; j < outputNeuron.getConnections().size(); j++) {
				outputNeuron.getConnections().get(j).setWeight(value);
			}
		}
		return this;
	}
	
	public NeuralNetwork setBiases(double value) {
		for (HiddenNeuron hiddenNeuron : this.hiddenNeurons) {
			hiddenNeuron.setBiasValue(value);
		}
		for (OutputNeuron outputNeuron : this.outputNeurons) {
			outputNeuron.setBiasValue(value);
		}
		return this;
	}
	
	public NeuralNetwork createNetwork() {
		for (HiddenNeuron hiddenNeuron : this.hiddenNeurons) {
			for (InputNeuron inputNeuron : this.inputNeurons) {
				hiddenNeuron.addConnection(new Connection(Math.random(), inputNeuron, hiddenNeuron));
			}
		}
		for (OutputNeuron outputNeuron : this.outputNeurons) {
			for (HiddenNeuron hiddenNeuron : this.hiddenNeurons) {
				outputNeuron.addConnection(new Connection(Math.random(), hiddenNeuron, outputNeuron));
			}
		}
		return this;
	}

	public void train(double learningRate, double[] expected, int maxIter) {
		for(int iter = 0; iter < maxIter; iter++) {
			this.trainBackpropagation(learningRate, expected);
		}
	}

	private void trainBackpropagation(double learningRate, double[] expected) {
		double[] errorOutputs = new double[this.outputNeurons.size()];
		double[] errorHidden = new double[this.hiddenNeurons.size()];

		for(int i = 0; i < this.outputNeurons.size(); i++) {
			errorOutputs[i] = (expected[i] - this.outputNeurons.get(i).getValue()) * (this.outputNeurons.get(i).getValue() * (1.0 - this.outputNeurons.get(i).getValue()));
		}
		for(int i = 0; i < this.hiddenNeurons.size(); i++) {
			for(int j = 0; j < this.outputNeurons.size(); j++) {
				errorHidden[i] += errorOutputs[j] * this.outputNeurons.get(j).getConnections().get(i).getWeight();
			}
			errorHidden[i] *= this.hiddenNeurons.get(i).getValue() * (1.0 - this.hiddenNeurons.get(i).getValue());
		}
		for(int i = 0; i < this.outputNeurons.size(); i++) {
			for(int j = 0; j < this.hiddenNeurons.size(); j++) {
				this.outputNeurons.get(i).getConnections().get(j).addWeight((learningRate * errorOutputs[i] * this.hiddenNeurons.get(j).getValue()));
			}
		}
		for(int i = 0; i < this.hiddenNeurons.size(); i++) {
			for(int j = 0; j < this.inputNeurons.size(); j++) {
				this.hiddenNeurons.get(i).getConnections().get(j).addWeight((learningRate * errorHidden[i]));
			}
		}
	}
	
	public double[] getOutputs() {
		double[] outputs = new double[this.outputNeurons.size()];
		for(int i = 0; i < this.outputNeurons.size(); i++) {
			outputs[i] = this.outputNeurons.get(i).getValue();
		}
		return outputs;
	}
	
	public NeuralNetwork printOutputs() {
		for(double value : this.getOutputs()) {
			this.printer.printConsole("Output: " + value);
		}
		return this;
	}
	
	public NeuralNetwork hiddenFunction(Functions function) {
		for (HiddenNeuron hiddenNeuron : this.hiddenNeurons) {
			hiddenNeuron.setFunction(function);
		}
		return this;
	}
	
	public NeuralNetwork outputFunction(Functions function) {
		for (OutputNeuron outputNeuron : this.outputNeurons) {
			outputNeuron.setFunction(function);
		}
		return this;
	}
	
	public NeuralNetwork addInputNeuron(double inputValue) {
		InputNeuron inputNeuron = new InputNeuron();
		inputNeuron.setValue(inputValue);
		this.inputNeurons.add(inputNeuron);
		return this;
	}
	
	public NeuralNetwork addHiddenNeuron() {
		this.hiddenNeurons.add(new HiddenNeuron());
		return this;
	}
	
	public NeuralNetwork addOutputNeuron() {
		this.outputNeurons.add(new OutputNeuron());
		return this;
	}

	public NeuralNetwork setInputNeurons(int size) {
		for(int i = 0; i < size; i++) {
			this.inputNeurons.add(new InputNeuron());
		}
		return this;
	}

	public NeuralNetwork setHiddenNeurons(int size) {
		for(int i = 0; i < size; i++) {
			this.hiddenNeurons.add(new HiddenNeuron());
		}
		return this;
	}
	
	public NeuralNetwork setOutputNeurons(int size) {
		for(int i = 0; i < size; i++) {
			this.outputNeurons.add(new OutputNeuron());
		}
		return this;
	}

	public List<InputNeuron> getInputNeurons() {
		return inputNeurons;
	}
}
