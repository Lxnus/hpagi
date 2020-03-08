package de.linusschmidt.hpagi.sar;

import de.linusschmidt.hpagi.core.memory.Hopfield;
import de.linusschmidt.hpagi.utilities.MathUtilities;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SAR {

    private Loader loader;
    private Printer printer;

    private List<Hopfield> memories;

    public SAR() {
        this.loader = new Loader();
        this.printer = new Printer();
    }

    private void learn(double[][] x, double[] y) {
        LinkedList<double[]> histories = MathUtilities.cbr2DRL(x, 0, 2, y[0], y[1]);
        for(double[] history : histories) {
            Utilities.printVector(history);
        }
    }

    private double[] toLength(double[] x, int length) {
        double[] buffer = new double[length];
        for(int i = 0; i < length; i++) {
            if(i < x.length) {
                buffer[i] = x[i];
            } else {
                buffer[i] = 0.0D;
            }
        }
        return buffer;
    }

    public static void main(String[] args) {
        SAR sar = new SAR();
        // Zustandsveränderung
        double[][] x = new double[][] {
                { 2, 4 },
                { 0, 1 },
                { 2, 5 },
                { -1, -1 },
        };
        double[] y = new double[] {
                32, 23
        };

        sar.learn(x, y);
    }
}
