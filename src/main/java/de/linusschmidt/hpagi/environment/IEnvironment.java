package de.linusschmidt.hpagi.environment;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public interface IEnvironment {

    double[] possibleActions();

    void apply(double inputs);

    double requestReward(double futureState);

    double getReward();

    boolean isFinish();

    void reset();
}
