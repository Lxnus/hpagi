package de.linusschmidt.hpagi.genetic;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class DNA {

    private double[] dna;

    public DNA(double[] dna) {
        this.dna = dna;
    }

    public DNA crossover(DNA target) {
        int length = Math.min(this.dna.length, target.dna.length);
        double[] dna = new double[length];
        for(int i = 0; i < length; i++) {
            if(Math.random() < 0.5D) {
                dna[i] = this.dna[i];
            } else {
                dna[i] = target.dna[i];
            }
        }
        return new DNA(dna);
    }

    public void mutation(double mutationRate) {
        for(int i = 0; i < this.dna.length; i++) {
            if(mutationRate > Math.random()) {
                this.dna[i] = Math.random();
            }
        }
    }

    public DNA sizeCrossover(DNA target) {
        int size = Math.round((this.dna.length + target.dna.length) / 2.0F);
        double[] dna = new double[size];
        for(int i = 0; i < size; i++) {
            if(i < this.dna.length && i < target.dna.length) {
                if(Math.random() < 0.5D) {
                    dna[i] = this.dna[i];
                } else {
                    dna[i] = target.dna[i];
                }
            } else if(i < this.dna.length && i >= target.dna.length) {
                dna[i] = this.dna[i];
            } else if(i >= this.dna.length && i < target.dna.length) {
                dna[i] = target.dna[i];
            }
        }
        return new DNA(dna);
    }

    public void sizeMutation(double mutationRate) {
        if(Math.random() < mutationRate) {
            int v = Math.random() < 0.5D ? -1 : 1;
            int size = this.dna.length - v * (int) (Math.random() * this.dna.length + 1);
            size = size <= 0 ? 1 : size;
            double[] newDNA = new double[size];
            for(int i = 0; i < newDNA.length; i++) {
                if(newDNA.length < this.dna.length) {
                    newDNA[i] = this.dna[(this.dna.length / newDNA.length) * i];
                } else {
                    if(i < this.dna.length) {
                        newDNA[i] = this.dna[i];
                    } else {
                        newDNA[i] = Math.random();
                    }
                }
            }
            this.dna = newDNA;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for(double value : this.dna) {
            str.append(Math.round(value));
        }
        str.append("]");
        return str.toString();
    }

    public double[] getDna() {
        return dna;
    }
}
