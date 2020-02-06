package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.translation.Translator;

public class Agent {

    private Translator translator;

    public Agent(Translator translator) {
        translator = translator;
    }

    public double[] getBinaryState() {
        double[] binaryH = new double[4];
        for(int i = 0; i < binaryH.length; i++) {
            binaryH[i] = Math.random() < 0.5D ? 1 : 0;
        }
        return new double[] {0, 0, 0, 0};
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
