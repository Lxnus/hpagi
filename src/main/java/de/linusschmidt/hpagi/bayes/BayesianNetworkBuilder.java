package de.linusschmidt.hpagi.bayes;

import com.bayesserver.*;
import com.bayesserver.data.*;
import com.bayesserver.inference.*;
import com.bayesserver.learning.parameters.*;
import com.bayesserver.learning.structure.*;
import de.linusschmidt.hpagi.translation.Translator;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class BayesianNetworkBuilder {

    private String[] nodeDescription;
    private String[] dataDescriptions;

    private Network network;
    private Printer printer;
    private Translator translator;

    private LinkedList<double[]> data;

    public BayesianNetworkBuilder() {
        this.network = new Network();
        this.printer = new Printer();
        this.translator = new Translator();
    }

    /**
     * Übergibt alle notwendigen Parameter dem Builder.
     * @param nodeDescription beschreibt, welche eigenschaft die Knoten haben können (default = 'True', 'False')
     * @param dataDescriptions sagt aus, wie die Knoten genannt werden (default= 'A', 'B', ...).
     * @param data gibt die Daten an, die Verwendet werden um das Modell zu bauen.
     */
    public void setData(String[] nodeDescription, String[] dataDescriptions, LinkedList<double[]> data) {
        this.data = data;
        this.nodeDescription = nodeDescription;
        this.dataDescriptions = dataDescriptions;


        // Speichert die Beschreibungen der Knoten im Übersetzter ab.
        // Der Übersetzer ist hier für den Builder eigen.
        for(String dataDescription : this.dataDescriptions) {
            this.translator.add(dataDescription);
        }
    }

    /**
     * Generiert das Bayes'sche Netz.
     * Dieser Prozess kann je nach Größe einige Zeit dauern.
     * In der Methode sind alle anderen 'build'-Methoden inbegriffen.
     */
    public void generateBayesianNetwork() {
        this.printer.printConsole("Generating bayesian network...");
        this.build();
        this.network.validate(new ValidationOptions());
        this.printNetwork();
        this.printer.printConsole("done. - Generate bayesian network!");
    }

    /**
     * Sagt einen Status vorraus. Dieser wird berechnet, in dem die Tabelle, die Vewendet werden soll 'idx' angegeben
     * wird und die anderen Kausalitäten.
     * @param binaryInputs gibt die Bedingungen an. Zum Beispiel: A = 'True', D = 'False'
     * @param idx gibt die Tabelle an, die bei der Berechnung verwendet werden soll.
     * @throws InconsistentEvidenceException
     */
    public void predict(double[] binaryInputs, int idx) throws InconsistentEvidenceException {
        InferenceFactory inferenceFactory = new RelevanceTreeInferenceFactory();
        Inference inference = inferenceFactory.createInferenceEngine(this.network);
        QueryOutput queryOutput = inferenceFactory.createQueryOutput();
        QueryOptions queryOptions = inferenceFactory.createQueryOptions();

        queryOptions.setDecisionAlgorithm(DecisionAlgorithm.SINGLE_POLICY_UPDATING);

        LinkedList<Table> tables = new LinkedList<>();
        for(int i = 0; i < binaryInputs.length; i++) {
            if(binaryInputs[i] == 1 && i != idx) {
                Variable variable = inference.getNetwork().getVariables().get(this.dataDescriptions[i]);
                State state = variable.getStates().get(this.processedNodeData(binaryInputs)[idx]);
                inference.getEvidence().setState(state);
            }
            Node node = inference.getNetwork().getNodes().get(this.dataDescriptions[i]);
            Table table = new Table(node.getDistribution().getTable());
            inference.getQueryDistributions().add(table);
            tables.add(table);
        }

        inference.query(queryOptions, queryOutput);

        Table table = tables.get(idx);
        State[] states = new State[table.getSortedVariables().size()];
        for (int i = 0; i < table.getSortedVariables().size(); i++) {
            Variable variable = table.getSortedVariables().get(i).getVariable();
            String strState = Math.round(binaryInputs[this.translator.get(variable.getName())]) == 0 ? "False" : "True";
            State state = variable.getStates().get(strState);
            states[i] = state;
        }
        double prediction = table.get(states);
        this.printer.printConsole(String.format("Prediction: %s", prediction));

        /*
        InferenceFactory inferenceFactory = new RelevanceTreeInferenceFactory();
        Inference inference = inferenceFactory.createInferenceEngine(this.network);
        QueryOutput queryOutput = inferenceFactory.createQueryOutput();
        QueryOptions queryOptions = inferenceFactory.createQueryOptions();

        queryOptions.setDecisionAlgorithm(DecisionAlgorithm.SINGLE_POLICY_UPDATING);

        LinkedList<Table> tables = new LinkedList<>();
        for(int i = 0; i < binaryInputs.length; i++) {
            if(binaryInputs[i] == 1 && i != idx) {
                Variable variable = inference.getNetwork().getVariables().get(this.dataDescriptions[i]);
                State state = variable.getStates().get(this.processedNodeData(binaryInputs)[i]);
                inference.getEvidence().setState(state);
            }
            Node node = inference.getNetwork().getNodes().get(this.dataDescriptions[i]);
            Table table = new Table(node.getDistribution().getTable());
            inference.getQueryDistributions().add(table);
            tables.add(table);
        }
        inference.query(queryOptions, queryOutput);

        int index = 0;
        for(Table table : tables) {
            if(idx == index) {
                State[] states = new State[table.getSortedVariables().size()];
                for (int i = 0; i < table.getSortedVariables().size(); i++) {
                    Variable variable = table.getSortedVariables().get(i).getVariable();
                    String strState = Math.round(binaryInputs[this.translator.get(variable.getName())]) == 0 ? "False" : "True";
                    State state = variable.getStates().get(strState);
                    states[i] = state;
                }
                double prediction = table.get(states);
                this.printer.printConsole(String.format("Prediction: %s", prediction));
            }
            index++;
        }
        int index = 0;
        double result = 0.0D;
        double[] output = new double[this.dataDescriptions.length];
        for(String dataDescription : this.dataDescriptions) {
            double smallResult = 0.0D;
            Node node = inference.getNetwork().getNodes().get(dataDescription);
            Table table = new Table(node.getDistribution().getTable());
            for(int i = 0; i < binaryInputs.length; i++) {
                State[] states = new State[table.getSortedVariables().size()];
                for(int j = 0; j < table.getSortedVariables().size(); j++) {
                    for(int k = 0; k < states.length; k++) {
                        Variable current = table.getSortedVariables().get(j).getVariable();
                        states[k] = current.getStates().get(idx == 0 ? "False" : "True");
                    }
                }
                double prediction = table.get(states);
                smallResult += prediction;
                this.printer.printConsole(String.format("Prediction: %s", prediction));
            }
            result += smallResult;
            output[index++] = Math.round(smallResult / binaryInputs.length);
            this.printer.printConsole(String.format("Small-Result: %s", (smallResult / binaryInputs.length)));
        }
        this.printer.printConsole(String.format("Result: %s", (result / (this.dataDescriptions.length * binaryInputs.length))));
        Utilities.printVector(output);
        */
    }

    private void build() {
        this.printer.printConsole("Build network...");

        // Bereitet die Daten für das Netzwerk vor.
        DataTable dataTable = this.generateDataTable(this.data);
        DataTableDataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);

        // Lässt die Knoten für das Netzwerk erstellen.
        this.generateNetworkNodes();

        // Jedes Netz muss bei der API 'Bayesserver' über 'VariableReferences' verfügen. Diese werden mithilfe
        // der oben erstellten Knoten erstellt.
        List<VariableReference> variableReferences = new ArrayList<>();
        for(Variable variable : this.network.getVariables()) {
            variableReferences.add(new VariableReference(variable, ColumnValueType.NAME, variable.getName()));
        }
        EvidenceReaderCommand evidenceReaderCommand = this.generateEvidence(dataReaderCommand, variableReferences);

        // Erstellt die Netzwerk Stuktur. Welche Knoten mit welchen Verbunden werden.
        this.createNetworkStructure(evidenceReaderCommand);


        // Erstellt die Wahrscheinlichketstabellen. Die Wahrscheinlichkeitstabellen sind erforderlich um den
        // Knoten zu sagen, mit welcher Wahrscheinlichkeit die Eigenschaften, die es besitzt eintreffen.
        // Diese Wahrscheinlichkeiten werden in bei Vorraussagen verwendet und benötigt.
        this.createDistributions(evidenceReaderCommand);
        this.printer.printConsole("done. - Build network!");
    }

    /**
     * Erstellt die Knoten für das Netz.
     */
    private void generateNetworkNodes() {
        for(String clazz : this.dataDescriptions) {
            Node node = new Node(clazz, this.nodeDescription);
            this.network.getNodes().add(node);
        }
    }

    /**
     * Erstellt die Struktur des Netzes.
     * @param evidenceReaderCommand
     */
    private void createNetworkStructure(EvidenceReaderCommand evidenceReaderCommand) {
        /*
        PCStructuralLearning structuralLearning = new PCStructuralLearning();
        PCStructuralLearningOptions structuralLearningOptions = new PCStructuralLearningOptions();
        PCStructuralLearningOutput structuralLearningOutput =
                (PCStructuralLearningOutput) structuralLearning.learn(
                        evidenceReaderCommand,
                        this.network.getNodes(),
                        structuralLearningOptions);
        */
        this.printer.printConsole("Create network structure...");

        // Erstellt den Lernalgorithmus, welcher verwendet werden soll.
        ChowLiuStructuralLearning chowLiuStructuralLearning = new ChowLiuStructuralLearning();

        // Erstellt die Lernoptionen, die eingestellt werden können.
        ChowLiuStructuralLearningOptions chowLiuStructuralLearningOptions = new ChowLiuStructuralLearningOptions();

        // Der Lernausgang, kann anzeigen, welche Knoten verbunden worden sind etc..
        ChowLiuStructuralLearningOutput chowLiuStructuralLearningOutput =
                (ChowLiuStructuralLearningOutput) chowLiuStructuralLearning.learn(
                        evidenceReaderCommand,
                        this.network.getNodes(),
                        chowLiuStructuralLearningOptions);

        // Gibt die Knoten aus, die verbunden worden sind.
        for(LinkOutput linkOutput : chowLiuStructuralLearningOutput.getLinkOutputs()) {
            this.printer.printConsole(String.format("Link added from %s -> %s",
                    linkOutput.getLink().getFrom().getName(),
                    linkOutput.getLink().getTo().getName()));
        }
        this.printer.printConsole("done. - Create network structure!");
    }

    /**
     * Erstellt die Wahrscheinlichkeitstabllen, die benötigt werden um später berechnungen durchzuführen.
     * @param evidenceReaderCommand
     */
    private void createDistributions(EvidenceReaderCommand evidenceReaderCommand) {
        this.printer.printConsole("Create distributions...");
        // Erstellt den Lernalgorithmus, welcher verwendet werden soll.
        ParameterLearning parameterLearning = new ParameterLearning(this.network, new RelevanceTreeInferenceFactory());

        // Erstellt die Lernoptionen, die eingestellt werden können.
        ParameterLearningOptions parameterLearningOptions = new ParameterLearningOptions();

        // Setzt den Entscheidungsalgorithmus auf: Wahrscheinlichkeiten.
        parameterLearningOptions.setDecisionPostProcessing(DecisionPostProcessingMethod.PROBABILITIES);

        // Setzt auf die Optionen, das die Parameter Convergiert werden.
        parameterLearningOptions.setConvergenceMethod(ConvergenceMethod.PARAMETERS);

        // Lässt den Algorithmus die Wahrscheinlichkeit unter dem festgelegten Lernalgorithmus und Optionen erlernen.
        parameterLearning.learn(evidenceReaderCommand, parameterLearningOptions);
        this.printer.printConsole("done. - Create distributions!");
    }

    private EvidenceReaderCommand generateEvidence(DataTableDataReaderCommand dataReaderCommand, List<VariableReference> variableReferences) {
        return new DefaultEvidenceReaderCommand(dataReaderCommand, variableReferences, new ReaderOptions());
    }

    /**
     * Generiert aus einer LinkedList eine Datentabelle, die Verwendet wird, um das Netzwerk zu bauen.
     * Beispiel:
     *
     * A | B | C
     * ----------
     * T | F | T
     * F | T | F
     * T | T | T
     * F | T | T
     *
     * T = True (1)
     * F = False (0)
     *
     * @param data repräsentiert die Daten. Diese können universell aufgebaut sein.
     * @return gibt die fertig erstellte DatenTabelle zurück
     */
    private DataTable generateDataTable(LinkedList<double[]> data) {
        this.printer.printConsole("Generate data table...");
        // Erstellt die DatenTabelle (Leer).
        DataTable dataTable = new DataTable();

        // Erstellt die Spalten für die Daten.
        DataColumnCollection dataColumns = dataTable.getColumns();
        for(String dataDescription : this.dataDescriptions) {
            dataColumns.add(dataDescription, String.class);
        }

        // Erstellt die Spalten für die Daten.
        DataRowCollection dataRows = dataTable.getRows();
        for(double[] vector : data) {
            // Da die Daten noch binäre Vektoren sind, müssen diese in 'True', 'False' umgewandelt werden.
            String[] processedNodeData = this.processedNodeData(vector);
            dataRows.add(processedNodeData);
        }
        this.printer.printConsole("done. - Generating data table!");
        return dataTable;
    }

    /**
     *
     * @param preData
     * @return
     */
    private String[] processedNodeData(double[] preData) {
        String[] processed = new String[preData.length];
        for(int i = 0; i < processed.length; i++) {
            processed[i] = this.nodeDescription[(int) preData[i]];
        }
        return processed;
    }

    private void printNetwork() {
        for(Node node : this.network.getNodes()) {
            this.printer.printConsole(String.format("Node[%s]:", node.getName()));
            this.printer.printConsoleSL("> Links: ");
            if(node.getLinks().size() != 0) {
                System.out.println();
                for (Link link : node.getLinks()) {
                    this.printer.printConsole(String.format("   Link: %s -> %s", link.getFrom().getName(), link.getTo().getName()));
                }
            } else {
                this.printer.printConsole("No links.");
            }
            this.printer.printConsole("> Distributions: ");
            for(int i = 0; i < node.getDistribution().getTable().size(); i++) {
                this.printer.printConsole(String.format("    => [%s]: %s", i, node.getDistribution().getTable().get(i)));
            }
        }
    }
}
