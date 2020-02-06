package de.linusschmidt.hpagi.bayes;

import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.ValidationOptions;
import com.bayesserver.Variable;
import com.bayesserver.data.*;
import com.bayesserver.data.sampling.DataSampler;
import com.bayesserver.data.sampling.DataSamplingOptions;
import com.bayesserver.inference.*;
import com.bayesserver.learning.parameters.OnlineLearning;
import com.bayesserver.learning.parameters.OnlineLearningOptions;
import com.bayesserver.learning.parameters.ParameterLearning;
import com.bayesserver.learning.parameters.ParameterLearningOptions;
import com.bayesserver.learning.structure.LinkOutput;
import com.bayesserver.learning.structure.PCStructuralLearning;
import com.bayesserver.learning.structure.PCStructuralLearningOptions;
import com.bayesserver.learning.structure.PCStructuralLearningOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BayesianNetworkBuilder {

    private String[] nodeDescription;
    private String[] dataDescriptions;

    private Network network;

    private List<double[]> data;

    public BayesianNetworkBuilder() {
        this.network = new Network();
    }

    public void setData(String[] nodeDescription, String[] dataDescriptions, List<double[]> data) {
        this.data = data;
        this.nodeDescription = nodeDescription;
        this.dataDescriptions = dataDescriptions;
    }

    public Network generateBayesianNetwork() {
        try {
            this.build();
        } catch (InconsistentEvidenceException e) {
            e.printStackTrace();
        }
        this.network.validate(new ValidationOptions());
        return this.network;
    }

    public void predict(double[] binaryInput) throws InconsistentEvidenceException {
        Evidence evidence = new DefaultEvidence(this.network);
        //for(int i = 0; i < binaryInput.length; i++) {
            evidence.setState(this.network.getVariables()
                    .get(this.dataDescriptions[0])
                    .getStates()
                    .get(this.processedNodeData(binaryInput)[0], Boolean.parseBoolean(this.processedNodeData(binaryInput)[0])));
        //}

        Evidence sample = new DefaultEvidence(this.network);

        DataSampler dataSampler = new DataSampler(this.network, evidence);
        DataSamplingOptions dataSamplingOptions = new DataSamplingOptions();

        for(int i = 0; i < 100; i++) {
            dataSampler.takeSample(sample, new Random(0), dataSamplingOptions);

            System.out.println("A: " + this.network.getVariables().get("A").getStates().get(sample.getState(this.network.getVariables().get("A"))).getName());
            System.out.println("B: " + this.network.getVariables().get("B").getStates().get(sample.getState(this.network.getVariables().get("B"))).getName());
            System.out.println("C: " + this.network.getVariables().get("B").getStates().get(sample.getState(this.network.getVariables().get("C"))).getName());
            System.out.println();
        }
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
        PCStructuralLearning structuralLearning = new PCStructuralLearning();
        PCStructuralLearningOptions structuralLearningOptions = new PCStructuralLearningOptions();
        PCStructuralLearningOutput structuralLearningOutput = (PCStructuralLearningOutput) structuralLearning.learn(evidenceReaderCommand, this.network.getNodes(), structuralLearningOptions);
        for(LinkOutput linkOutput : structuralLearningOutput.getLinkOutputs()) {
            System.out.println(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
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
        DataRowCollection rows = dataTable.getRows();
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("False", "True", "True");
        rows.add("False", "False", "False");
        rows.add("False", "True", "True");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("False", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("False", "True", "True");
        rows.add("True", "True", "True");
        rows.add("False", "True", "True");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("False", "False", "False");
        rows.add("False", "False", "False");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("False", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        rows.add("False", "True", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("False", "False", "False");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("False", "False", "False");
        rows.add("False", "True", "True");
        rows.add("True", "False", "True");
        rows.add("True", "False", "True");
        rows.add("True", "True", "True");
        rows.add("True", "True", "True");
        rows.add("False", "True", "True");
        rows.add("True", "True", "True");
        rows.add("True", "False", "True");
        /*
        for(double[] datum : data) {
            String[] procData = this.processedNodeData(datum);
            dataRows.add(procData[0], procData[1], procData[2]);
        }
        */
        return dataTable;
    }

    private String[] processedNodeData(double[] preData) {
        String[] processed = new String[preData.length];
        for(int i = 0; i < processed.length; i++) {
            processed[i] = preData[i] == 1 ? "True" : "False";
        }
        return processed;
    }
}
