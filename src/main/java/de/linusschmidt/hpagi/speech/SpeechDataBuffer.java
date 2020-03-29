package de.linusschmidt.hpagi.speech;

import de.linusschmidt.hpagi.utilities.NLPUtilities;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.ArrayList;
import java.util.List;

public class SpeechDataBuffer {

    private String text;

    private List<String> sentences;

    private List<List<String>> tokens;

    public SpeechDataBuffer(String text) {
        this.text = text;

        this.sentences = new ArrayList<>();
        this.tokens = new ArrayList<>();

        this.convert(text);
    }

    public SpeechDataBuffer(String text, List<String> sentences, List<List<String>> tokens) {
        this.text = text;
        this.sentences = sentences;
        this.tokens = tokens;
    }

    private void convert(String text) {
        this.sentences = NLPUtilities.sentenceDetector(text);
        for(String sentence : this.sentences) {
            this.tokens.add(NLPUtilities.tokenize(sentence));
        }
    }

    public void print() {
        Printer printer = new Printer();
        printer.printConsole(this.text);
        printer.printConsole("");
        for(String sentence : this.sentences) {
            printer.printConsole(sentence);
        }
        printer.printConsole("");
        for(List<String> tokens : this.tokens) {
            printer.printConsole(tokens.toString());
        }
    }

    public String getText() {
        return text;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public List<List<String>> getTokens() {
        return tokens;
    }
}
