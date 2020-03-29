package de.linusschmidt.hpagi.utilities;

import java.util.HashMap;

public class Dictionary {

    private double dictionaryIndex = 0;

    private HashMap<String, Double> dictionary = null;
    private HashMap<Double, String> reverseDictionary = null;

    public Dictionary() {
        this.dictionary = new HashMap<>();
        this.reverseDictionary = new HashMap<>();
    }

    public void put(String word) {
        Double index = this.dictionary.get(word);
        if(index == null) {
            this.dictionary.put(word, this.dictionaryIndex);
            this.reverseDictionary.put(this.dictionaryIndex, word);
            this.dictionaryIndex++;
        }
    }

    public String get(Double index) {
        return this.reverseDictionary.get(index);
    }

    public Double get(String word) {
        return this.dictionary.get(word);
    }

    public int size() {
        return this.dictionary.size();
    }
}
