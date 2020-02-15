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

public class BayesianNetworkBuilder {

    private String[] nodeDescription;
    private String[] dataDescriptions;

    private Network network;
    private Printer printer;
    private Translator translator;

    private List<double[]> data;

    public BayesianNetworkBuilder() {
        this.network = new Network();
        this.printer = new Printer();
        this.translator = new Translator();
    }

    public void setData(String[] nodeDescription, String[] dataDescriptions, List<double[]> data) {
        this.data = data;
        this.nodeDescription = nodeDescription;
        this.dataDescriptions = dataDescriptions;

        for(String dataDescription : this.dataDescriptions) {
            this.translator.add(dataDescription);
        }
    }

    public void generateBayesianNetwork() {
        this.printer.printConsole("Generating bayesian network...");
        this.build();
        this.network.validate(new ValidationOptions());
        this.printNetwork();
        this.printer.printConsole("done. - Generate bayesian network!");
    }

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
        DataTable dataTable = this.generateDataTable(this.data);
        DataTableDataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);
        this.generateNetworkNodes();

        List<VariableReference> variableReferences = new ArrayList<>();
        for(Variable variable : this.network.getVariables()) {
            variableReferences.add(new VariableReference(variable, ColumnValueType.NAME, variable.getName()));
        }
        EvidenceReaderCommand evidenceReaderCommand = this.generateEvidence(dataReaderCommand, variableReferences);

        this.createNetworkStructure(evidenceReaderCommand);
        this.createDistributions(evidenceReaderCommand);
        this.printer.printConsole("done. - Build network!");
    }

    private void generateNetworkNodes() {
        for(String clazz : this.dataDescriptions) {
            Node node = new Node(clazz, this.nodeDescription);
            this.network.getNodes().add(node);
        }
    }

    private void createNetworkStructure(EvidenceReaderCommand evidenceReaderCommand) {
        /*
        PCStructuralLearning structuralLearning = new PCStructuralLearning();
        PCStructuralLearningOptions structuralLearningOptions = new PCStructuralLearningOptions();
        PCStructuralLearningOutput structuralLearningOutput = (PCStructuralLearningOutput) structuralLearning.learn(evidenceReaderCommand, this.network.getNodes(), structuralLearningOptions);
        for(LinkOutput linkOutput : structuralLearningOutput.getLinkOutputs()) {
            System.out.println(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
        }
        */
        this.printer.printConsole("Create network structure...");
        ChowLiuStructuralLearning chowLiuStructuralLearning = new ChowLiuStructuralLearning();
        ChowLiuStructuralLearningOptions chowLiuStructuralLearningOptions = new ChowLiuStructuralLearningOptions();
        ChowLiuStructuralLearningOutput chowLiuStructuralLearningOutput = (ChowLiuStructuralLearningOutput) chowLiuStructuralLearning.learn(evidenceReaderCommand, this.network.getNodes(), chowLiuStructuralLearningOptions);
        for(LinkOutput linkOutput : chowLiuStructuralLearningOutput.getLinkOutputs()) {
            this.printer.printConsole(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
        }
        this.printer.printConsole("done. - Create network structure!");
    }

    private void createDistributions(EvidenceReaderCommand evidenceReaderCommand) {
        this.printer.printConsole("Create distributions...");
        ParameterLearning parameterLearning = new ParameterLearning(this.network, new RelevanceTreeInferenceFactory());
        ParameterLearningOptions parameterLearningOptions = new ParameterLearningOptions();
        parameterLearningOptions.setDecisionPostProcessing(DecisionPostProcessingMethod.PROBABILITIES);
        parameterLearningOptions.setConvergenceMethod(ConvergenceMethod.PARAMETERS);
        parameterLearning.learn(evidenceReaderCommand, parameterLearningOptions);
        this.printer.printConsole("done. - Create distributions!");
    }

    private EvidenceReaderCommand generateEvidence(DataTableDataReaderCommand dataReaderCommand, List<VariableReference> variableReferences) {
        return new DefaultEvidenceReaderCommand(dataReaderCommand, variableReferences, new ReaderOptions());
    }

    private DataTable generateDataTable(List<double[]> data) {
        this.printer.printConsole("Generate data table...");
        DataTable dataTable = new DataTable();
        DataColumnCollection dataColumns = dataTable.getColumns();
        for(String dataDescription : this.dataDescriptions) {
            dataColumns.add(dataDescription, String.class);
        }
        DataRowCollection dataRows = dataTable.getRows();
        for(double[] vector : data) {
            String[] processedNodeData = this.processedNodeData(vector);
            dataRows.add(processedNodeData);
        }
        this.printer.printConsole("done. - Generating data table!");
        return dataTable;
    }

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
