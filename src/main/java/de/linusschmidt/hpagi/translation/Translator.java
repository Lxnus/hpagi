package de.linusschmidt.hpagi.translation;

import de.linusschmidt.hpagi.utilities.Printer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Translator {

    private AtomicInteger idx;

    private Printer printer;

    private ConcurrentHashMap<String, Integer> translation;
    private ConcurrentHashMap<Integer, String> reverseTranslation;

    public Translator() {
        this.printer = new Printer();

        this.build();
    }

    private void build() {
        this.idx = new AtomicInteger(0);

        this.translation = new ConcurrentHashMap<>();
        this.reverseTranslation = new ConcurrentHashMap<>();
    }

    public synchronized void add(String key) {
        Integer reference = this.translation.get(key);
        if(reference == null) {
            this.translation.put(key, this.idx.getAndIncrement());
            this.reverseTranslation.put(this.idx.get(), key);
        }
    }

    public synchronized Integer get(String key) {
        return this.translation.get(key);
    }

    public synchronized String get(Integer value) {
        return this.reverseTranslation.get(value);
    }

    public void print() {
        this.printer.printConsole(String.format("Translation: %s", this.translation));
    }
}
