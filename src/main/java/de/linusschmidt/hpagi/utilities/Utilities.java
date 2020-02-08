package de.linusschmidt.hpagi.utilities;

public class Utilities {

    private static Printer printer = new Printer();

    public static void printVector(double[] vector) {
        Utilities.printer.printConsoleSL("Vector: [");
        for(int i = 0; i < vector.length; i++) {
            if(i < vector.length - 1) {
                System.out.print(vector[i] + ", ");
            } else {
                System.out.print(vector[i] + "]");
            }
        }
        System.out.println();
    }
}
