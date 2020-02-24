package de.linusschmidt.hpagi.sar;

import de.linusschmidt.hpagi.utilities.Dictionary;
import de.linusschmidt.hpagi.utilities.NLPUtilities;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.List;

public class Loader {

    private Printer printer;
    private Dictionary dictionary;

    public Loader() {
        this.printer = new Printer();
        this.dictionary = new Dictionary();
    }

    public void createDictionary(String text) {
        List<String> tokens = NLPUtilities.tokenize(text);
        for(String token : tokens) {
            this.dictionary.put(token);
        }
        this.printer.printConsole(String.format("Dictionary: %s words.", this.dictionary.size()));
    }

    public double[] getBinary(String token) {
        double[] binary = new double[this.dictionary.size()];
        for(int i = 0; i < binary.length; i++) {
            if(i == this.dictionary.get(token)) {
                binary[i] = 1;
            }
        }
        return binary;
    }

    public double[] getBinary(List<String> tokens) {
        double[] binary = new double[this.dictionary.size()];
        for(int i = 0; i < binary.length; i++) {
            for(String token : tokens) {
                if(i == this.dictionary.get(token)) {
                    binary[i] = 1;
                }
            }
        }
        return binary;
    }

    public String getCode(double[] binary) {
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < binary.length; i++) {
            if(binary[i] == 1) {
                code.append(this.dictionary.get((double) i)).append(" ");
            }
        }
        return code.toString();
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public static void main(String[] args) {
        Loader loader = new Loader();
        loader.createDictionary("");
    }
}
