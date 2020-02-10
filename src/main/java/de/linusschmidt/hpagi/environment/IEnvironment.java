package de.linusschmidt.hpagi.environment;

public interface IEnvironment {

    double[] possibleActions();

    void apply(double inputs);

    double requestReward(double futureState);

    double getReward();

    boolean isFinish();

    void reset();
}
