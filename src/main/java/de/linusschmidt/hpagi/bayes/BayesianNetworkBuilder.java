package de.linusschmidt.hpagi.bayes;

import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.data.*;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;
import com.bayesserver.learning.parameters.OnlineLearning;
import com.bayesserver.learning.parameters.OnlineLearningOptions;
import de.linusschmidt.hpagi.translation.Translator;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.List;

public class BayesianNetworkBuilder {

    private String[] nodeDescription;

    private Printer printer;
    private Network network;
    private Translator translator;

    private List<double[]> data;
    private List<String> dataDescriptions;

    public BayesianNetworkBuilder(Translator translator) {
        this.translator = translator;

        this.printer = new Printer();
        this.network = new Network();
    }

    public void setData(String[] nodeDescription, List<String> dataDescriptions, List<double[]> data) {
        this.data = data;
        this.nodeDescription = nodeDescription;
        this.dataDescriptions = dataDescriptions;
    }

    public Network generateBayesianNetwork() {
        return this.network;
    }

    private void build() {
        DataTable dataTable = this.generateDataTable(this.data);
        DataTableDataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);
    }

    private void generateNetworkNodes(int amount) {
        if(amount > this.data.size()) {
            this.printer.printConsoleError("Node amount cannot be larger than data size!");
        }
        for(int i = 0; i < amount; i++) {
            Node node = new Node(String.valueOf((int) Math.round(Math.random() * Integer.MAX_VALUE)), this.nodeDescription);
        }
    }

    private void createDistributions() {
        OnlineLearning onlineLearning = new OnlineLearning(this.network, new RelevanceTreeInferenceFactory());
        OnlineLearningOptions onlineLearningOptions = new OnlineLearningOptions();
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
            dataRows.add(datum);
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
