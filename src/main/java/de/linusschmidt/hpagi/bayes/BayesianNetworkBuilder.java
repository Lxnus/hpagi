package de.linusschmidt.hpagi.bayes;

import com.bayesserver.Network;
import com.bayesserver.data.*;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;
import com.bayesserver.learning.parameters.OnlineLearning;
import com.bayesserver.learning.parameters.OnlineLearningOptions;
import de.linusschmidt.hpagi.utilities.Printer;

import java.util.List;

public class BayesianNetworkBuilder {

    private List<double[]> data;

    private Printer printer;
    private Network network;

    public BayesianNetworkBuilder() {
        this.printer = new Printer();

        this.network = new Network();
    }

    public void setData(List<double[]> data) {
        this.data = data;
    }

    public Network generateBayesianNetwork() {
        return this.network;
    }

    private void build(double[][] data) {

    }

    private void createDistributions() {
        OnlineLearning onlineLearning = new OnlineLearning(this.network, new RelevanceTreeInferenceFactory());
        OnlineLearningOptions onlineLearningOptions = new OnlineLearningOptions();
    }

    private EvidenceReaderCommand generateEvidence(DataTableDataReaderCommand dataReaderCommand, List<VariableReference> variableReferences) {
        return new DefaultEvidenceReaderCommand(dataReaderCommand, variableReferences, new ReaderOptions());
    }

    private DataTable generateDataTable(double[][] data) {
        return null;
    }
}
