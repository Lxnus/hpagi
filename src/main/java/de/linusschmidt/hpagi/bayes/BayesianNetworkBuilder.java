package de.linusschmidt.hpagi.bayes;

import com.bayesserver.*;
import com.bayesserver.data.*;
import com.bayesserver.inference.*;
import com.bayesserver.learning.parameters.OnlineLearning;
import com.bayesserver.learning.parameters.OnlineLearningOptions;
import com.bayesserver.learning.parameters.ParameterLearning;
import com.bayesserver.learning.parameters.ParameterLearningOptions;
import com.bayesserver.learning.structure.*;
import de.linusschmidt.hpagi.utilities.Printer;
import de.linusschmidt.hpagi.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BayesianNetworkBuilder {

    private String[] nodeDescription;
    private String[] dataDescriptions;

    private Network network;
    private Printer printer;

    private List<double[]> data;

    public BayesianNetworkBuilder() {
        this.network = new Network();
        this.printer = new Printer();
    }

    public void setData(String[] nodeDescription, String[] dataDescriptions, List<double[]> data) {
        this.data = data;
        this.nodeDescription = nodeDescription;
        this.dataDescriptions = dataDescriptions;
    }

    public void generateBayesianNetwork() {
        try {
            this.build();
        } catch (InconsistentEvidenceException e) {
            e.printStackTrace();
        }
        this.network.validate(new ValidationOptions());
        this.printNetwork();
    }

    public void predict(double[] binaryInputs, int idx) throws InconsistentEvidenceException {
        InferenceFactory inferenceFactory = new RelevanceTreeInferenceFactory();
        Inference inference = inferenceFactory.createInferenceEngine(this.network);
        QueryOutput queryOutput = inferenceFactory.createQueryOutput();
        QueryOptions queryOptions = inferenceFactory.createQueryOptions();

        queryOptions.setDecisionAlgorithm(DecisionAlgorithm.SINGLE_POLICY_UPDATING);

        for(int i = 0; i < binaryInputs.length; i++) {
            if(binaryInputs[i] == 1 && i != idx) {
                Variable variable = inference.getNetwork().getVariables().get(this.dataDescriptions[i]);
                State state = variable.getStates().get(this.processedNodeData(binaryInputs)[i]);
                inference.getEvidence().setState(state);
            }
            Node node = inference.getNetwork().getNodes().get(this.dataDescriptions[i]);
            Table table = new Table(node.getDistribution().getTable());
            inference.getQueryDistributions().add(table);
        }
        inference.query(queryOptions, queryOutput);

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
                        states[k] = current.getStates().get(Math.round(Math.random())/*idx*/ == 0 ? "False" : "True");
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
    }

    private void build() throws InconsistentEvidenceException {
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

        ChowLiuStructuralLearning chowLiuStructuralLearning = new ChowLiuStructuralLearning();
        ChowLiuStructuralLearningOptions chowLiuStructuralLearningOptions = new ChowLiuStructuralLearningOptions();
        ChowLiuStructuralLearningOutput chowLiuStructuralLearningOutput = (ChowLiuStructuralLearningOutput) chowLiuStructuralLearning.learn(evidenceReaderCommand, this.network.getNodes(), chowLiuStructuralLearningOptions);
        for(LinkOutput linkOutput : chowLiuStructuralLearningOutput.getLinkOutputs()) {
            this.printer.printConsole(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
        }
    }

    private void createDistributions(EvidenceReaderCommand evidenceReaderCommand) throws InconsistentEvidenceException {
        ParameterLearning parameterLearning = new ParameterLearning(this.network, new RelevanceTreeInferenceFactory());
        ParameterLearningOptions parameterLearningOptions = new ParameterLearningOptions();
        parameterLearning.learn(evidenceReaderCommand, parameterLearningOptions);

        Evidence evidence = new DefaultEvidence(this.network);
        OnlineLearning onlineLearning = new OnlineLearning(this.network, new RelevanceTreeInferenceFactory());
        OnlineLearningOptions onlineLearningOptions = new OnlineLearningOptions();
        onlineLearning.adapt(evidence, onlineLearningOptions);


    }

    private EvidenceReaderCommand generateEvidence(DataTableDataReaderCommand dataReaderCommand, List<VariableReference> variableReferences) {
        return new DefaultEvidenceReaderCommand(dataReaderCommand, variableReferences, new ReaderOptions());
    }

    private DataTable generateDataTable(List<double[]> data) {
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
