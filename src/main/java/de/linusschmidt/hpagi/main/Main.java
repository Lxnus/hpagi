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

    public static void main(String[] args) throws InterruptedException {
        translationTest();
        bayesianTest();
    }

    public static void bayesianTest() {
        DataTable dataTable = new DataTable();
        DataColumnCollection dataColumns = dataTable.getColumns();
        dataColumns.add("A", String.class);
        dataColumns.add("B", String.class);
        dataColumns.add("C", String.class);
        DataRowCollection dataRows = dataTable.getRows();
        for(int i = 0; i < 1000; i++) {
            String[] states = new String[3];
            for(int j = 0; j < states.length; j++) {
                String state = Math.random() < 0.5D ? "True" : "False";
                states[j] = state;
            }
            dataRows.add(states[0], states[1], states[2]);
        }

        Network network = new Network();
        String[] states = new String[] { "True", "False" };
        String[] classes = new String[] { "A", "B", "C" };
        for(String clazz : classes) {
            Node node = new Node(clazz, states);
            network.getNodes().add(node);
        }

        PCStructuralLearning structuralLearning = new PCStructuralLearning();
        DataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);
        List<VariableReference> variableReferences = new ArrayList<>();
        for(Variable v : network.getVariables()) {
            variableReferences.add(new VariableReference(v, ColumnValueType.NAME, v.getName()));
        }
        EvidenceReaderCommand evidenceReaderCommand = new DefaultEvidenceReaderCommand(
                dataReaderCommand,
                variableReferences,
                new ReaderOptions());
        PCStructuralLearningOptions structuralLearningOptions = new PCStructuralLearningOptions();
        PCStructuralLearningOutput structuralLearningOutput = (PCStructuralLearningOutput) structuralLearning.learn(evidenceReaderCommand, network.getNodes(), structuralLearningOptions);
        for(LinkOutput linkOutput : structuralLearningOutput.getLinkOutputs()) {
            System.out.println(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
        }
    }

    public static void translationTest() throws InterruptedException {
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
}
