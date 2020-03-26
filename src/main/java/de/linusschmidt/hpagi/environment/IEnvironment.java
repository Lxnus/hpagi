package de.linusschmidt.hpagi.environment;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public interface IEnvironment {

    double[] possibleActions();

    void apply(double inputs);

    double getReward();

    boolean isFinish();

    void reset();
}
