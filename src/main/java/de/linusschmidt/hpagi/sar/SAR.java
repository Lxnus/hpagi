package de.linusschmidt.hpagi.sar;

import de.linusschmidt.hpagi.network.Hopfield;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.Arrays;
import java.util.List;

public class SAR {

    private Loader loader;
    private Printer printer;

    private List<Hopfield> memories;

    public SAR() {
        this.loader = new Loader();
        this.printer = new Printer();
    }

    public void build(List<String> code, List<String> testCode) {
        StringBuilder iCode = new StringBuilder();
        for(String c : code) {
            iCode.append(c).append(" ");
        }
        StringBuilder iTestCode = new StringBuilder();
        for(String tc : testCode) {
            iTestCode.append(tc).append(" ");
        }
        this.loader.createDictionary(iCode.toString());
        this.loader.createDictionary(iTestCode.toString());


        double[] array = this.loader.getBinary(code);
        Hopfield hopfield = new Hopfield(array.length);
        hopfield.addData(array);

        hopfield.train();

        double[] testArray = loader.getBinary(testCode);

        double[] testRecreation = hopfield.recreate(testArray, 1000);

        Utilities.printVector(array);
        Utilities.printVector(testArray);
        Utilities.printVector(testRecreation);

        this.printer.printConsole(String.format("Test-Code: %s", loader.getCode(testRecreation)));
    }

    public static void main(String[] args) {
        SAR sar = new SAR();

        sar.build(Arrays.asList("public", "static", "void", "test", "(", ")", "{", "int", "a", "=", "Math", ".", "random", "(", ")", ";", "}"),
                Arrays.asList("public", "double", "test", "(", ")", "{", "int", "a", "=", "Math", ".", "random", "(", ")", ";", "return", "a", ";", "}"));
    }
}
