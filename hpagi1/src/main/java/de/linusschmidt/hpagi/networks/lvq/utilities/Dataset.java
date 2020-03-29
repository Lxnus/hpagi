package de.linusschmidt.hpagi.networks.lvq.utilities;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Dataset {

    private String category;

    private double[] vector;

    public Dataset(String category, double[] vector) {
        this.category = category;
        this.vector = vector;
    }

    public String getCategory() {
        return category;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }
}
