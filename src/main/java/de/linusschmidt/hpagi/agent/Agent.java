package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Printer;

public class Agent {

    private Printer printer;
    private CoreEngine coreEngine;

    public Agent() {
        this.printer = new Printer();
    }

    public void setEnvironment(IEnvironment environment) {
        this.coreEngine = new CoreEngine(environment);
    }

    public void run() {
        double reward = this.coreEngine.run(1000);
        this.printer.printConsole(String.format("Reward: %s", reward));
    }
}
