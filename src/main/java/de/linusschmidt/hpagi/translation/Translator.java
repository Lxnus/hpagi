package de.linusschmidt.hpagi.translation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Translator {

    private AtomicInteger idx;

    private ConcurrentHashMap<AtomicReference<String>, AtomicInteger> translation;

    public Translator() {
        this.build();
    }

    private void build() {
        this.idx = new AtomicInteger(0);

        this.translation = new ConcurrentHashMap<>();
    }

    public synchronized void add(AtomicReference<String> key) {
        System.out.println(System.currentTimeMillis());
        AtomicInteger reference = this.translation.get(key);
        if(reference == null) {
            this.translation.put(key, new AtomicInteger(this.idx.getAndIncrement()));
        }
    }

    public synchronized AtomicInteger get(AtomicReference<String> key) {
        return this.translation.get(key);
    }

    public void print() {
        System.out.println(this.translation);
    }
}
