package de.linusschmidt.hpagi.sar;

import de.linusschmidt.hpagi.core.networks.neuralnet.neuralnetwork.NeuralNetwork;
import de.linusschmidt.hpagi.core.networks.neuralnet.utilities.Functions;
import de.linusschmidt.hpagi.environment.Environment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.LinkedList;

public class SAR {

    private SAR() {
        Environment environment = new Environment();

        LinkedList<DataBuffer> dataBuffers = new LinkedList<>();

        for(int i = 0; i < 1000; i++) {
            environment.apply((int) (Math.random() * 4));
            dataBuffers.add(this.toBinaryState(environment.getLastRecord(), environment));
        }

        double[][] translation = new double[][] {
                { -1, 0, 1 },
                { 0.5, 0, 1}
        };

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
                neuralNetwork.train(0.9D, temp.binaryState, 25);
            }
        }
    }

    private double[] output(double[] x, double[][] translation) {
        double[] normalValues = translation[0];
        double[] translationValues = translation[1];

        int[] indices = new int[x.length];
        for(int i = 0; i < x.length; i++) {
            int idx = -1;
            double min = Double.MAX_VALUE;
            for(int j = 0; j < translationValues.length; j++) {
                double distance = Math.sqrt(Math.pow(translationValues[j] - x[i], 2));
                if(distance < min) {
                    min = distance;
                    idx = j;
                }
            }
            indices[i] = idx;
        }
        double[] output = new double[x.length];
        for(int i = 0; i < output.length; i++) {
            output[i] = normalValues[indices[i]];
        }
        return output;
    }

    private DataBuffer toBinaryState(double[] record, Environment environment) {
        double[] binaryAction = new double[environment.possibleActions().length];
        for(int i = 0; i < binaryAction.length; i++) {
            if(record[0] == i) {
                binaryAction[i] = 1;
            }
        }

        double[] binaryState = new double[] {
                record[3] - record[1] < 0 ? 0.5 : record[3] - record[1],
                record[4] - record[2] < 0 ? 0.5 : record[4] - record[2]
        };

        return new DataBuffer(binaryState, binaryAction);
    }

    private static class DataBuffer {

        private double[] binaryState;
        private double[] binaryAction;

        private DataBuffer(double[] binaryState, double[] binaryAction) {
            this.binaryState = binaryState;
            this.binaryAction = binaryAction;
        }

        public void print() {
            Utilities.printVector(this.binaryState);
            Utilities.printVector(this.binaryAction);
        }
    }

    public static void main(String[] args) {
        SAR sar = new SAR();
    }
}
