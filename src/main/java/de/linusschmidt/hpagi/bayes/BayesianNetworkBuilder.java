package de.linusschmidt.hpagi.bayes;

import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.Variable;
import com.bayesserver.data.*;
import com.bayesserver.inference.DefaultEvidence;
import com.bayesserver.inference.Evidence;
import com.bayesserver.inference.InconsistentEvidenceException;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;
import com.bayesserver.learning.parameters.OnlineLearning;
import com.bayesserver.learning.parameters.OnlineLearningOptions;
import com.bayesserver.learning.parameters.ParameterLearning;
import com.bayesserver.learning.parameters.ParameterLearningOptions;
import com.bayesserver.learning.structure.PCStructuralLearning;
import com.bayesserver.learning.structure.PCStructuralLearningOptions;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.ArrayList;
import java.util.List;

public class BayesianNetworkBuilder {

    private String[] nodeDescription;
    private String[] dataDescriptions;

    private Printer printer;
    private Network network;

    private List<double[]> data;

    public BayesianNetworkBuilder() {
        this.printer = new Printer();
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
        return this.network;
    }

    private void build() throws InconsistentEvidenceException {
        DataTable dataTable = this.generateDataTable(this.data);
        DataTableDataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);
        this.generateNetworkNodes(this.data.size() / 2);

        List<VariableReference> variableReferences = new ArrayList<>();
        for(Variable variable : this.network.getVariables()) {
            variableReferences.add(new VariableReference(variable, ColumnValueType.NAME, variable.getName()));
        }
        EvidenceReaderCommand evidenceReaderCommand = this.generateEvidence(dataReaderCommand, variableReferences);

        this.createNetworkStructure(evidenceReaderCommand);
        this.createDistributions(evidenceReaderCommand);
    }

    private void generateNetworkNodes(int amount) {
        if(amount > this.data.size()) {
            this.printer.printConsoleError("Node amount cannot be larger than data size!");
        }
        for(String clazz : this.dataDescriptions) {
            Node node = new Node(clazz, this.nodeDescription);
            this.network.getNodes().add(node);
        }
    }

    private void createNetworkStructure(EvidenceReaderCommand evidenceReaderCommand) {
        PCStructuralLearning structuralLearning = new PCStructuralLearning();
        PCStructuralLearningOptions structuralLearningOptions = new PCStructuralLearningOptions();
        structuralLearning.learn(evidenceReaderCommand, this.network.getNodes(), structuralLearningOptions);
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
            dataColumns.add(dataDescription, Double.class);
        }
        DataRowCollection dataRows = dataTable.getRows();
        for(double[] datum : data) {
            String[] procData = this.processedNodeData(datum);
            dataRows.add(procData[0], procData[1]);
        }
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
