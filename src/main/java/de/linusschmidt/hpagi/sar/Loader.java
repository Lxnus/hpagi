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

    public Dictionary getDictionary() {
        return dictionary;
    }

    public static void main(String[] args) {
        Loader loader = new Loader();
        loader.createDictionary("");
    }
}
