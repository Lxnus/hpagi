package de.linusschmidt.hpagi.genetic;

import de.linusschmidt.hpagi.utilities.MathUtilities;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class NetGA {

    private int inputs;
    private int hidden;
    private int outputs;

    private DNA[] hiddenWeights;
    private DNA[] outputWeights;

    public NetGA(int nIn, int nHidden, int nOut) {
        this.inputs = nIn;
        this.hidden = nHidden;
        this.outputs = nOut;

        this.hiddenWeights = new DNA[this.hidden];
        this.outputWeights = new DNA[this.outputs];

        for(int i = 0; i < this.hidden; i++) {
            double[] weights = new double[this.inputs];
            for(int j = 0; j < weights.length; j++) {
                weights[j] = Math.random();
            }
            this.hiddenWeights[i] = new DNA(weights);
        }
        for(int i = 0; i < this.outputs; i++) {
            double[] weights = new double[this.hidden];
            for(int j = 0; j < weights.length; j++) {
                weights[j] = Math.random();
            }
            this.outputWeights[i] = new DNA(weights);
        }
    }

    public double[] output(double[] X) {
        if(this.inputs != X.length) {
            System.err.println("Check length!");
            System.exit(-1);
        }
        double[] bufferH = new double[this.hidden];
        for(int i = 0; i < bufferH.length; i++) {
            double sum = 0.0D;
            for(int j = 0; j < X.length; j++) {
                sum += X[j] * this.hiddenWeights[i].getDna()[j];
            }
            bufferH[i] = MathUtilities.sigmoid(sum);
        }
        double[] bufferO = new double[this.outputs];
        for(int i = 0; i < bufferO.length; i++) {
            double sum = 0.0D;
            for(int j = 0; j < bufferH.length; j++) {
                sum += bufferH[j] * this.outputWeights[i].getDna()[j];
            }
            bufferO[i] = MathUtilities.tanh(sum);
        }
        return MathUtilities.softmax(bufferO);
    }

    public void crossover(NetGA target) {
        if(this.inputs != target.inputs || this.hidden != target.hidden || this.outputs != target.outputs) {
            System.err.println("Check length!");
            System.exit(-1);
        }
        for(int i = 0; i < this.hidden; i++) {
            DNA child = this.hiddenWeights[i].crossover(target.hiddenWeights[i]);
            this.hiddenWeights[i] = child;
        }
        for(int i = 0; i < this.outputs; i++) {
            DNA child = this.outputWeights[i].crossover(target.outputWeights[i]);
            this.outputWeights[i] = child;
        }
    }

    public void mutate(double mutationRate) {
        for (DNA hiddenWeight : this.hiddenWeights) {
            hiddenWeight.mutation(mutationRate);
        }
        for (DNA outputWeight : this.outputWeights) {
            outputWeight.mutation(mutationRate);
        }
    }
}
