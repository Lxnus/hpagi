package de.linusschmidt.hpagi.agent;

import de.linusschmidt.hpagi.bayes.BayesianNetworkBuilder;
import de.linusschmidt.hpagi.core.CoreEngine;
import de.linusschmidt.hpagi.environment.IEnvironment;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.LinkedList;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Agent {

    private CoreEngine coreEngine;
    private BayesianNetworkBuilder bayesianNetworkBuilder;

    public Agent() {
        this.bayesianNetworkBuilder = new BayesianNetworkBuilder();
    }

    /**
     * Übergibt eine Umgebung, in der sich der Agent frei bewegen und handeln kann.
     * @param environment die selbst kreierte Umgebung.
     */
    public void setEnvironment(IEnvironment environment) {
        this.coreEngine = new CoreEngine(environment);
    }

    /**
     * Die startet den Agenten. Dieser lässt zunächst das Problem von der Umgebung mit einer 'CoreEngine' lösen.
     * Danach erstellt er mittels der Extrahierung der Daten aus dem Kompressierten MC-Baumes ein Bayes'sches Netz.
     */
    public void run() {
        /**
         * Die CoreEngine mit dem MCTS-Algorithmus versucht das Problem (das finden der max. Belohnung),
         * zu maximieren und zu läsen. Der Paramter übergibt die min. Genauigkeit, die der Baum erreichen soll.
         */
        this.coreEngine.solve(0.95); // parameter: accuracy

        /**
         * Entnimmt dem Agent die Trainingsdaten.
         * Die Trainingsdaten repräsentieren den Weg mit zur max. Belohnung.
         */
        LinkedList<double[]> trainingData = this.coreEngine.getTrainingData();
        if(trainingData != null) {
            for(double[] data : trainingData) {
                Utilities.printVector(data);
            }
            /**
             * Übergibt die erforderlichen Parameter dem BayesianNetworkBuilder.
             * Zurzeit noch nicht Automatisiert. Kommt aber bald.
             * Jeder Knoten hat die Werte 'True' oder 'False' und die Knoten sind mit 'A', 'B', 'C' und 'D' gekennzeichnet.
             */
            this.bayesianNetworkBuilder.setData(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, trainingData);

            /**
             * Erstellt das Bayes'sche Netz!
             */
            this.bayesianNetworkBuilder.generateBayesianNetwork();
        }
    }
}
