package de.linusschmidt.hpagi.core.networks.lvq;

import de.linusschmidt.hpagi.core.networks.lvq.utilities.Dataset;
import de.linusschmidt.hpagi.utilities.MathUtilities;

import java.io.*;
import java.util.*;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class LVQNetwork {

    private int vectorSize;

    private double learningRate;

    private double[] minValues;
    private double[] maxValues;

    private LinkedList<Dataset> inputLayer;
    private LinkedList<Dataset> outputLayer;

    public LVQNetwork(int vectorSize, double learningRate) {
        this.vectorSize = vectorSize;

        this.learningRate = learningRate;

        this.inputLayer = new LinkedList<>();
        this.outputLayer = new LinkedList<>();
    }

    public void init() {
        for(Dataset input : this.inputLayer) {
            double[] outputVector = new double[this.vectorSize];
            for (int j = 0; j < outputVector.length; j++) {
                outputVector[j] = Math.random();
            }
            Dataset dataset = new Dataset(input.getCategory(), outputVector);
            this.outputLayer.add(dataset);
        }
    }

    public void normalize() {
        this.minValues = new double[this.vectorSize];
        this.maxValues = new double[this.vectorSize];
        for(int i = 0; i < this.vectorSize; i++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for(Dataset dataset : this.inputLayer) {
                double data = dataset.getVector()[i];
                min = Math.min(data, min);
                max = Math.max(data, max);
            }
            this.minValues[i] = min;
            this.maxValues[i] = max;
            for(Dataset dataset : this.inputLayer) {
                double x = dataset.getVector()[i];
                dataset.getVector()[i] = MathUtilities.normalize(x, min, max);
            }
        }
    }

    private double[] normalizeData(double[] vector) {
        double[] normalizedVector = new double[vector.length];
        for(int i = 0; i < vector.length; i++) {
            normalizedVector[i] = MathUtilities.normalize(vector[i], this.minValues[i], this.maxValues[i]);
        }
        return normalizedVector;
    }

    private int bmu(double[] input) {
        int minIndex = -1;
        double minDistance = Double.MAX_VALUE;
        for(int i = 0; i < this.inputLayer.size(); i++) {
            Dataset dataset = this.inputLayer.get(i);
            double distance = MathUtilities.distance(input, dataset.getVector());
            if(distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return minIndex;
    }

    public Dataset bmuDataset(double[] input) {
        int bmuIndex = this.bmu(input);
        return this.outputLayer.get(bmuIndex);
    }

    private void train() {
        for(Dataset input : this.inputLayer) {
            int bmuIndex = this.bmu(input.getVector());
            Dataset bmu = this.outputLayer.get(bmuIndex);
            if(input.getCategory().equalsIgnoreCase(bmu.getCategory())) {
                for(int i = 0; i < this.vectorSize; i++) {
                    this.outputLayer.get(bmuIndex).getVector()[i] += this.learningRate * (input.getVector()[i] - bmu.getVector()[i]);
                }
            } else {
                for(int i = 0; i < this.vectorSize; i++) {
                    this.outputLayer.get(bmuIndex).getVector()[i] -= this.learningRate * (input.getVector()[i] - bmu.getVector()[i]);
                }
            }
        }
    }

    public void train(int maxIter) {
        for(int iter = 0; iter < maxIter; iter++) {
            this.train();
        }
    }

    public String predict(double[] input) {
        double[] normalizedVector = this.normalizeData(input);
        int bmuIndex = this.bmu(normalizedVector);
        return this.outputLayer.get(bmuIndex).getCategory();
    }

    public void add(Dataset dataset) {
        this.inputLayer.add(dataset);
    }

    public void adapt(Dataset in, Dataset out) throws IOException {
        double[] normalizedVector = this.normalizeData(in.getVector());
        in.setVector(normalizedVector);
        this.inputLayer.add(in);
        Dataset outDataset = new Dataset(in.getCategory(), out.getVector());
        this.outputLayer.add(outDataset);
        this.train(1000);
        this.saveModel();
    }

    private void saveModel() throws IOException {
        File file = new File("models/Model_" + Math.random());
        if(!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for(Dataset inputNeuron : this.inputLayer) {
            bufferedWriter.write("I;" + inputNeuron.getCategory() + ";");
            for(double value : inputNeuron.getVector()) {
                bufferedWriter.write(value + ";");
            }
            bufferedWriter.newLine();
        }

        for(Dataset outputNeuron : this.outputLayer) {
            bufferedWriter.write(outputNeuron.getCategory() + ";");
            for(double value : outputNeuron.getVector()) {
                bufferedWriter.write("O;" + value + ";");
            }
            bufferedWriter.newLine();
        }
    }

    public void loadModel() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(Objects.requireNonNull(new File("models/").listFiles())[Objects.requireNonNull(new File("models/").listFiles()).length - 1]));
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            String[] splitter = line.split(";");
            String layer = splitter[0];
            String category = splitter[1];
            List<String> strValues = new ArrayList<>(Arrays.asList(splitter).subList(2, splitter.length));
            double[] array = new double[strValues.size()];
            for(int i = 0; i < array.length; i++) {
                array[i] = Double.parseDouble(strValues.get(i));
            }
            Dataset dataset = new Dataset(category, array);
            if(layer.equalsIgnoreCase("I")) {
                this.inputLayer.add(dataset);
            } else if(layer.equalsIgnoreCase("O")) {
                this.outputLayer.add(dataset);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Dataset d1 = new Dataset("0", new double[] { 0, 2, 3, 1, 2 });
        Dataset d2 = new Dataset("1", new double[] { 10, 4, 6, 9, 3 });

        LVQNetwork lvqNetwork = new LVQNetwork(d1.getVector().length, 0.9);
        lvqNetwork.add(d1);
        lvqNetwork.add(d2);
        lvqNetwork.init();
        lvqNetwork.normalize();
        lvqNetwork.train(2500);
        System.out.println(lvqNetwork.predict(new double[] { 0, 1, 1, 2, 0}));
        System.out.println(lvqNetwork.predict(new double[] { 0, 9, 1, 8, 7}));
        System.out.println(lvqNetwork.predict(new double[] { 3, 5, 1, 2, 0}));
        System.out.println();

        Dataset adaptDataset = new Dataset("1", new double[] { 9, 8, 3, 6, 10 });
        lvqNetwork.adapt(adaptDataset, lvqNetwork.bmuDataset(adaptDataset.getVector()));

        System.out.println(lvqNetwork.predict(new double[] { 0, 1, 1, 2, 0}));
        System.out.println(lvqNetwork.predict(new double[] { 0, 9, 1, 8, 7}));
        System.out.println(lvqNetwork.predict(new double[] { 3, 5, 1, 2, 0}));
        System.out.println();
    }
}
