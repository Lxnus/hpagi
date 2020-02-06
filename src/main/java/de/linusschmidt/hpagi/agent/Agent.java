package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.environment.Environment;
import de.linusschmidt.hpagi.translation.Translator;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private Translator translator;
    private Environment environment;

    private List<double[]> history;

    public Agent(Translator translator) {
        translator = translator;

        this.history = new ArrayList<>();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public double[] getBinaryState() {
        double[] binaryH = new double[4];
        for(int i = 0; i < binaryH.length; i++) {
            binaryH[i] = Math.random() < 0.5D ? 1 : 0;
        }
        System.out.println(this.environment.getReward());
        return binaryH; //new double[] {0, 0, 0, 0};
    }

    private void executeBinary(double[] binaryVec) {
        for(double binary : binaryVec) {
            if(binary != 0) {

            }
        }
    }

    private double[] generateHistory() {
        return null;
    }
}
