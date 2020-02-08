package de.linusschmidt.hpagi.translation;

import de.linusschmidt.hpagi.utilities.Printer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Translator {

    private AtomicInteger idx;

    private Printer printer;

    private ConcurrentHashMap<AtomicReference<String>, AtomicInteger> translation;
    private ConcurrentHashMap<AtomicInteger, AtomicReference<String>> reverseTranslation;

    public Translator() {
        this.printer = new Printer();

        this.build();
    }

    private void build() {
        this.idx = new AtomicInteger(0);

        this.translation = new ConcurrentHashMap<>();
        this.reverseTranslation = new ConcurrentHashMap<>();
    }

    public synchronized void add(AtomicReference<String> key) {
        this.printer.printConsole(String.format("CurrentTimeMillis: %s", System.currentTimeMillis()));
        AtomicInteger reference = this.translation.get(key);
        if(reference == null) {
            this.translation.put(key, new AtomicInteger(this.idx.getAndIncrement()));
            this.reverseTranslation.put(new AtomicInteger(this.idx.get()), key);
        }
    }

    public synchronized AtomicInteger get(AtomicReference<String> key) {
        return this.translation.get(key);
    }

    public synchronized AtomicReference<String> get(AtomicInteger value) {
        return this.reverseTranslation.get(value);
    }

    public void print() {
        this.printer.printConsole(String.format("Translation: %s", this.translation));
    }
}
