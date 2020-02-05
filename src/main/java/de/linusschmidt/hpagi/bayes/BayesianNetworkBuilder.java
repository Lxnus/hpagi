package de.linusschmidt.hpagi.bayes;

import de.linusschmidt.hpagi.utilities.Printer;

import java.util.List;

public class BayesianNetworkBuilder {

    private List<double[]> data;

    private Printer printer;

    public BayesianNetworkBuilder() {
        this.printer = new Printer();
    }

    public void setData(List<double[]> data) {
        this.data = data;
    }

    private void build() {

    }

    private void createDistributions() {

    }
}
