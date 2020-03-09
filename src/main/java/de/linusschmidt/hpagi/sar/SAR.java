package de.linusschmidt.hpagi.sar;

import de.linusschmidt.hpagi.core.networks.neuralnet.neuralnetwork.NeuralNetwork;
import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Functions;
import de.linusschmidt.hpagi.environment.Environment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.LinkedList;

public class SAR {

    public SAR() {
        Environment environment = new Environment();

        LinkedList<DataBuffer> dataBuffers = new LinkedList<>();

        for(int i = 0; i < 1000; i++) {
            environment.apply((int) (Math.random() * 4));
            dataBuffers.add(this.toBinaryState(environment.getLastRecord(), environment));
        }

        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setInputNeurons(4);
        neuralNetwork.setHiddenNeurons(5);
        neuralNetwork.setOutputNeurons(2);

        neuralNetwork.createNetwork();

        neuralNetwork.hiddenFunction(Functions.SIGMOID);
        neuralNetwork.outputFunction(Functions.SIGMOID);

        for(int iter = 0; iter < 100; iter++) {
            for (int i = 0; i < dataBuffers.size() - 1; i++) {
                DataBuffer temp = dataBuffers.get(i);
                for (int j = 0; j < temp.binaryAction.length; j++) {
                    neuralNetwork.getInputNeurons().get(j).setValue(temp.binaryAction[j]);
                }
                neuralNetwork.train(0.4D, temp.binaryState, 10);
            }
        }

        neuralNetwork.getInputNeurons().get(0).setValue(0);
        neuralNetwork.getInputNeurons().get(1).setValue(0);
        neuralNetwork.getInputNeurons().get(2).setValue(1);
        neuralNetwork.getInputNeurons().get(3).setValue(0);
        neuralNetwork.printOutputs();

        System.out.println();

        neuralNetwork.getInputNeurons().get(0).setValue(1);
        neuralNetwork.getInputNeurons().get(1).setValue(0);
        neuralNetwork.getInputNeurons().get(2).setValue(0);
        neuralNetwork.getInputNeurons().get(3).setValue(0);
        neuralNetwork.printOutputs();

        System.out.println();

        neuralNetwork.getInputNeurons().get(0).setValue(0);
        neuralNetwork.getInputNeurons().get(1).setValue(0);
        neuralNetwork.getInputNeurons().get(2).setValue(0);
        neuralNetwork.getInputNeurons().get(3).setValue(1);
        neuralNetwork.printOutputs();

    }

    private DataBuffer toBinaryState(double[] record, Environment environment) {
        double[] binaryAction = new double[environment.possibleActions().length];
        for(int i = 0; i < binaryAction.length; i++) {
            if(record[0] == i) {
                binaryAction[i] = 1;
            }
        }

        double[] binaryState = new double[] {
                record[3] - record[1],
                record[4] - record[2]
        };

        return new DataBuffer(binaryState, binaryAction);
    }

    private class DataBuffer {

        private double[] binaryState;
        private double[] binaryAction;

        private DataBuffer(double[] binaryState, double[] binaryAction) {
            this.binaryState = binaryState;
            this.binaryAction = binaryAction;
        }

        @Override
        public String toString() {
            Utilities.printVector(this.binaryState);
            Utilities.printVector(this.binaryAction);
            return "";
        }
    }

    public static void main(String[] args) {
        new SAR();
    }
}
