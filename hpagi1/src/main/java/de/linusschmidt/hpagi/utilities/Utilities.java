package de.linusschmidt.hpagi.utilities;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Utilities {

    private static Printer printer = new Printer();

    public static void printVector(double[] vector) {
        Utilities.printer.printConsoleSL("Vector: [");
        for(int i = 0; i < vector.length; i++) {
            if(i < vector.length - 1) {
                System.out.print(String.format("%s, ", vector[i]));
            } else {
                System.out.print(String.format("%s]", vector[i]));
            }
        }
        System.out.println();
    }

    public static void printVector(int[] vector) {
        Utilities.printer.printConsoleSL("Vector: [");
        for(int i = 0; i < vector.length; i++) {
            if(i < vector.length - 1) {
                System.out.print(String.format("%s, ", vector[i]));
            } else {
                System.out.print(String.format("%s]", vector[i]));
            }
        }
        System.out.println();
    }
}
