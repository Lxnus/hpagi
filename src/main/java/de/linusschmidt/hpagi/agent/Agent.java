package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.environment.IEnvironment;

public class Agent {

    private CoreEngine coreEngine;

    public Agent() {}

    public void setEnvironment(IEnvironment environment) {
        this.coreEngine = new CoreEngine(environment);
    }

    public void run() {
        this.coreEngine.run(100);
    }
}
