package de.linusschmidt.hpagi.environment;

public interface IEnvironment {

    void apply(double s);

    double[] possibleActions();

    void setPossibleActions(double[] actions);

    void setCoreTranslation(double[] actions);

    double getEnvAction(double coreAction);

    double getCoreAction(double environmentAction);

    double getReward();

    double optimization();

    boolean isFinish();

    void reset();
}
