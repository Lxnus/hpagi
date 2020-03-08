package de.linusschmidt.hpagi.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class MathUtilities {

    private static Printer printer = new Printer();

    public static double sigmoid(double inputValue) {
        return 1.0 / (1.0 + Math.exp(-inputValue));
    }

    public static double identity(double inputValue) {
        return inputValue;
    }

    public static double tanh(double inputValue) {
        return Math.tanh(inputValue);
    }

    public static double[] softmax(double[] input) {
        double[] S = new double[input.length];
        for(int i = 0; i < input.length; i++) {
            double v = 0.0D;
            for(double value : input) {
                v += Math.exp(value);
            }
            double r = Math.exp(input[i]) / v;
            S[i] = r;
        }
        return S;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    public static double distance(double[] x, double[] y) {
        if(x.length != y.length) {
            throw new IllegalArgumentException("Invalid array length");
        }
        int length = x.length;
        double sum = 0.0D;
        for(int i = 0; i < length; i++) {
            double buffer = y[i] - x[i];
            double distance = buffer * buffer;
            sum += distance;
        }
        return Math.sqrt(sum);
    }

    public static double normalize(double x, double min, double max) {
        return (x - min) / (max - min);
    }

    public static double standardDeviation(List<Double> values) {
        double mean = MathUtilities.mean(values);
        double output = 0.0D;
        for(double value : values) {
            double distance = value - mean;
            output += distance * distance;
        }
        return Math.sqrt(output / (double) values.size());
    }

    public static double deltaStandardDeviation(List<Double> values) {
        List<Double> deltaValues = MathUtilities.getDeltas(values);
        double mean = MathUtilities.deltaMean(values);
        double output = 0.0D;
        for(double value : deltaValues) {
            double distance = value - mean;
            output += distance * distance;
        }
        return Math.sqrt(output / MathUtilities.getDeltaLength(values));
    }

    public static List<Double> getDeltas(List<Double> values) {
        List<Double> deltas = new ArrayList<>();
        for(int i = 0; i < MathUtilities.getDeltaLength(values) - 1; i++) {
            deltas.add(Math.abs(values.get(i + 1) - values.get(i)));
        }
        return deltas;
    }

    public static double deltaMean(List<Double> values) {
        List<Double> deltas = MathUtilities.getDeltas(values);
        return (MathUtilities.sum(deltas) / MathUtilities.getDeltaLength(values));
    }

    public static double sum(List<Double> values) {
        double sum = 0.0D;
        for(Double value : values) {
            sum += value;
        }
        return sum;
    }

    public static int getDeltaLength(List<Double> values) {
        return values.size() - 1;
    }

    public static double mean(List<Double> values) {
        return MathUtilities.sum(values) / (double) values.size();
    }

    public static int cbr(double[] X, double x, double y) {
        int idx = -1;
        double d = y - x;
        double min = Double.MAX_VALUE;
        double[] D = new double[X.length];
        for(int i = 0; i < D.length; i++) {
            D[i] = X[i] - d;
            if(Math.abs(D[i]) < min) {
                min = Math.abs(D[i]);
                idx = i;
            }
        }
        return idx;
    }

    public static int cbr(double[] X, double[] Y, double x, double y) {
        if(X.length != Y.length) {
            System.err.println("Check length!");
            System.exit(-1);
        }
        double[] resX = new double[X.length];
        for(int i = 0; i < resX.length; i++) {
            resX[i] = Y[i] - X[i];
        }
        return MathUtilities.cbr(resX, x, y);
    }

    public static int cbr(double[][] mat, double x, double y) {
        double[] X = new double[mat.length];
        for(int i = 0; i < mat.length; i++) {
            double situation = 0.0D;
            for(int j = 0; j < mat[i].length; j++) {
                situation += mat[i][j];
            }
            X[i] = situation;
        }
        return MathUtilities.cbr(X, x, y);
    }

    public static void cbrRL(double[][] mat, double x, double y) {
        int steps = 0;
        do {
            double[] Y = mat[MathUtilities.cbr(mat, x, y)];
            double sum = 0.0D;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (double v : Y) {
                sum += v;
                if (v > max) {
                    max = v;
                }
                if (v < min) {
                    min = v;
                }
            }
            sum /= Y.length;
            sum = normalize(sum, min, max) * Y.length;
            MathUtilities.printer.printConsole(String.format("Move: %s", x));
            x += Y[(int) sum];
            steps++;
        } while (!(Math.abs(x) >= Math.abs(y)));
        MathUtilities.printer.printConsole(String.format("Steps: %s", steps));
        MathUtilities.printer.printConsole(String.format("%s -> %s", x, y));
    }

    /**
     * 2D
     */

    public static double[] cbr2D(double[][] mat, double pX, double pY, double tX, double tY) {
        double dx = tX - pX;
        double dy = tY - pY;
        int xIDX = -1;
        int yIDX = -1;
        double minDX = Double.MAX_VALUE;
        double minDY = Double.MAX_VALUE;
        for(int i = 0; i < mat.length; i++) {
            double bufferX = mat[i][0] - dx;
            double bufferY = mat[i][1] - dy;
            if(Math.abs(bufferX) < minDX) {
                minDX = Math.abs(bufferX);
                xIDX = i;
            }
            if(Math.abs(bufferY) < minDY) {
                minDY = Math.abs(bufferY);
                yIDX = i;
            }
        }
        return new double[] { mat[xIDX][0], mat[yIDX][1] };
    }

    public static void cbr2DRL(double[][] mat, double pX, double pY, double tX, double tY) {
        int steps = 0;
        do {
            double[] Y = MathUtilities.cbr2D(mat, pX, pY, tX, tY);
            double x = Y[0];
            double y = Y[1];
            pX += x;
            pY += y;
            MathUtilities.printer.printConsole(String.format("x-Move: %s", x));
            MathUtilities.printer.printConsole(String.format("y-Move: %s", y));
            steps++;
        } while (!(Math.abs(pX) >= Math.abs(tX)) || !(Math.abs(pY) >= Math.abs(tY)));
        MathUtilities.printer.printConsole(String.format("Steps: %s", steps));
        MathUtilities.printer.printConsole(String.format("x, y: %s, %s -> %s, %s", pX, pY, tX, tY));
    }

    /**
     * N
     */
    public static double[] cbrN(double[][] mat, double[] X, double[] Y) {
        double[] D = new double[Math.min(X.length, Y.length)];
        for(int i = 0; i < D.length; i++) {
            D[i] = Y[i] - X[i];
        }
        int[] indices = new int[Math.min(X.length, Y.length)];
        double[] min = new double[Math.min(X.length, Y.length)];
        Arrays.fill(indices, -1);
        Arrays.fill(min, Double.MAX_VALUE);
        for(int i = 0; i < mat.length; i++) {
            for(int j = 0; j < mat[i].length; j++) {
                double localMin = mat[i][j] - D[j];
                if(Math.abs(localMin) < min[j]) {
                    min[j] = Math.abs(localMin);
                    indices[j] = i;
                }
            }
        }
        double[] O = new double[Math.min(X.length, Y.length)];
        for(int i = 0; i < O.length; i++) {
            O[i] = mat[indices[i]][i];
        }
        return O;
    }

    public static void cbrNRL(double[][] mat, double[] X, double[] Y) {
        int steps = 0;
        while(true) {
            double[] O = MathUtilities.cbrN(mat, X, Y);
            for(int i = 0; i < O.length; i++) {
                X[i] += O[i];
                MathUtilities.printer.printConsole(String.format("n-Move[n=%s]: %s", i, O[i]));
            }
            steps++;
            boolean equal = true;
            for(int i = 0; i < X.length; i++) {
                if (Math.abs(X[i]) < Math.abs(Y[i])) {
                    equal = false;
                    break;
                }
            }
            if(equal) {
                break;
            }
        }
        MathUtilities.printer.printConsole(String.format("Steps: %s", steps));
    }
}
