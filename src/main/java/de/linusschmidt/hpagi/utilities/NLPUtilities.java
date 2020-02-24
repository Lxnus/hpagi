package de.linusschmidt.hpagi.utilities;

import smile.nlp.tokenizer.SimpleSentenceSplitter;
import smile.nlp.tokenizer.SimpleTokenizer;

import java.util.Arrays;
import java.util.List;

public class NLPUtilities {

    public static List<String> tokenize(String text) {
        SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
        String[] tokens = simpleTokenizer.split(text);
        return Arrays.asList(tokens);
    }

    public static List<String> sentenceDetector(String text) {
        SimpleSentenceSplitter sentenceDetector = SimpleSentenceSplitter.getInstance();
        String[] sentences = sentenceDetector.split(text);
        return Arrays.asList(sentences);
    }
}
