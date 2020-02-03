package de.linusschmidt.hpagi.main;

import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.Variable;
import com.bayesserver.data.*;
import com.bayesserver.learning.structure.LinkOutput;
import com.bayesserver.learning.structure.PCStructuralLearning;
import com.bayesserver.learning.structure.PCStructuralLearningOptions;
import com.bayesserver.learning.structure.PCStructuralLearningOutput;
import de.linusschmidt.hpagi.translation.Translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static String getFramework_Name() {
        return "HPAGI";
    }

    public static void main(String[] args) {

        Network network = CreateNetworkNoLinks();  // we manually construct the network here, but it could be loaded from a file

        PCStructuralLearning learning = new PCStructuralLearning();

        DataReaderCommand dataReaderCommand = CreateDataReaderCommand();

        List<VariableReference> variableReferences = new ArrayList<>();

        for (Variable v : network.getVariables()) {

            variableReferences.add(new VariableReference(v, ColumnValueType.NAME, v.getName()));
        }

        EvidenceReaderCommand evidenceReaderCommand = new DefaultEvidenceReaderCommand(
                dataReaderCommand,
                variableReferences,
                new ReaderOptions());

        PCStructuralLearningOptions options = new PCStructuralLearningOptions();

        PCStructuralLearningOutput output = (PCStructuralLearningOutput) learning.learn(evidenceReaderCommand, network.getNodes(), options);

        for (LinkOutput linkOutput : output.getLinkOutputs()) {

            System.out.println(
                    String.format("Link added from %s -> %s",
                            linkOutput.getLink().getFrom().getName(),
                            linkOutput.getLink().getTo().getName()));
        }

    }

    /// <summary>
    /// Manually construct a network to keep the example simple.
    /// </summary>
    private static Network CreateNetworkNoLinks() {

        Network network = new Network();

        // Instead of manually constructing a network
        // you could also load from a file using
        // network.Load("path-to-file");

        for (String name : new String[]{"A", "B", "C"}) {
            Node node = new Node(name, new String[]{"False", "True"});
            network.getNodes().add(node);
        }

        // We are not adding links here, as we are going to learn them from data.

        return network;
    }

    /// <summary>
    /// Create a data reader command.
    /// </summary>
    /// <remarks>
    /// Normally you would read from a database or spreadsheet, but to keep this example simple
    /// we are hard coding the data.
    /// </remarks>
    private static DataTableDataReaderCommand CreateDataReaderCommand() {

        // See the Parameter learning sample code, for an
        // example of how to read from a database.

        DataTable data = new DataTable();

        DataColumnCollection columns = data.getColumns();
        columns.add("A", String.class);
        columns.add("B", String.class);
        columns.add("C", String.class);

        DataRowCollection rows = data.getRows();
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

        return new DataTableDataReaderCommand(data);

    }

    /*
    public static void main(String[] args) throws InterruptedException {
        Translator translator = new Translator();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Collection<Callable<Void>> runnables = new CopyOnWriteArrayList<>();
        runnables.add(() -> {
            AtomicReference<String> text = new AtomicReference<>("Dies ist ein Text");
            translator.add(text);
            return null;
        });
        runnables.add(() -> {
            AtomicReference<String> text2 = new AtomicReference<>("Dies ist eine weiterer Text");
            translator.add(text2);
            return null;
        });
        executor.invokeAll(runnables);
        executor.shutdown();
        translator.print();
    }
    */
}
