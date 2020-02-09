package de.linusschmidt.hpagi.main;

import com.bayesserver.*;
import com.bayesserver.data.*;
import com.bayesserver.inference.InconsistentEvidenceException;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;
import com.bayesserver.learning.parameters.ParameterLearning;
import com.bayesserver.learning.parameters.ParameterLearningOptions;
import com.bayesserver.learning.parameters.ParameterLearningOutput;
import com.bayesserver.learning.structure.*;
import de.linusschmidt.hpagi.agent.Agent;
import de.linusschmidt.hpagi.cognitive.CognitiveAlgorithm;
import de.linusschmidt.hpagi.network.Hopfield;
import de.linusschmidt.hpagi.translation.Translator;
import de.linusschmidt.hpagi.utilities.Algorithms;
import de.linusschmidt.hpagi.utilities.Graph;
import de.linusschmidt.hpagi.utilities.Printer;
import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static Printer printer = new Printer();

    public static String getFramework_Name() {
        return "HPAGI";
    }

    public static void main(String[] args) throws Exception {
        translationTest();
        bayesianStructureTest();
        bayesianParameterTest();
        algorithmTest();
        prologTest();
        dynamicMemoryTest();
        cognitiveMultithreadingTest();
        //environmentTest();
    }

    private static void cognitiveMultithreadingTest() throws InterruptedException, InconsistentEvidenceException {
        List<double[]> data = new ArrayList<>();
        /*
        for(int i = 0; i < 5; i++) {
            double[] vector = new double[10];
            for(int j = 0; j < vector.length; j++) {
                vector[j] = Math.random() < 0.5D ? 1 : 0;
            }
            data.add(vector);
        }
        */

        data.add(new double[] { 0, 1, 1, 0 });
        data.add(new double[] { 1, 0, 0, 1 });
        data.add(new double[] { 1, 1, 0, 0 });
        data.add(new double[] { 0, 0, 1, 1 });
        data.add(new double[] { 1, 0, 0, 1 });

        double[] X = new double[] {
                1, 0, 1, 0
        };

        CognitiveAlgorithm cognitiveAlgorithm = new CognitiveAlgorithm();
        cognitiveAlgorithm.setData(data);
        cognitiveAlgorithm.generateBayesianNetwork(new String[] {"True", "False"}, new String[] {"A", "B", "C", "D"}, data);
        cognitiveAlgorithm.print();
        cognitiveAlgorithm.cognitivePrediction(X);
    }

    private static void dynamicMemoryTest() {
        String number = "00111000" +
                        "11011000" +
                        "00011000" +
                        "00011000" +
                        "11111111";
        double[] vector = new double[number.split("").length];
        for(int i = 0; i < vector.length; i++) {
            vector[i] = Double.parseDouble(number.split("")[i]);
        }
        Hopfield dynamicMemory = new Hopfield(vector.length);
        dynamicMemory.addData(vector);
        dynamicMemory.addData(vector);

        dynamicMemory.train();

        String testNumber = "00111100" +
                            "11011011" +
                            "00011000" +
                            "00011000" +
                            "11111111";
        double[] testVector = new double[testNumber.split("").length];
        for(int i = 0; i < testVector.length; i++) {
            testVector[i] = Double.parseDouble(number.split("")[i]);
        }

        double[] recall = dynamicMemory.recreate(testVector, 10);
        StringBuilder out = new StringBuilder();
        for(double value : recall) {
            String recallNumber = value != 1 ? "0" : "1";
            out.append(recallNumber);
        }
        Main.printer.printConsole(out.toString());
    }

    private static void environmentTest() {
        Agent agent = new Agent(null);
        new de.linusschmidt.hpagi.environment.Environment(agent);
    }

    private static void prologTest() {
        Environment environment = new Environment();
        environment.ensureLoaded(AtomTerm.get("main.pro"));

        Interpreter interpreter = environment.createInterpreter();

        environment.runInitialization(interpreter);

        for(PrologTextLoaderError error : environment.getLoadingErrors()) {
            error.printStackTrace();
        }
    }

    private static void algorithmTest() {
        Graph graph = new Graph();
        Graph.Node a = new Graph.Node(0);
        Graph.Node b = new Graph.Node(1);
        Graph.Node c = new Graph.Node(2);
        Graph.Node d = new Graph.Node(3);
        Graph.Node e = new Graph.Node(4);

        d.addNeighbour(e);
        b.addNeighbour(d);
        b.addNeighbour(c);
        a.addNeighbour(b);
        a.addNeighbour(c);

        graph.addNode(a);

        Algorithms algorithms = new Algorithms();
        Graph.Node result = algorithms.bfs(graph, 3);
        Main.printer.printConsole(String.format("Result: %s", result.getState()));
    }

    private static void bayesianStructureTest() {
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
            Main.printer.printConsole(String.format("Link added from %s -> %s", linkOutput.getLink().getFrom().getName(), linkOutput.getLink().getTo().getName()));
        }
    }

    private static void bayesianParameterTest() {
        DataTable dataTable = new DataTable();
        DataColumnCollection dataColumns = dataTable.getColumns();
        dataColumns.add("X", Double.class);
        dataColumns.add("Y", Double.class);
        DataRowCollection dataRows = dataTable.getRows();
        /*
        for(int i = 0; i < 10; i++) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            System.out.println("(x, y) -> " + x + ", " + y);
            dataRows.add(x, y);
        }
        */
        /*
         * Hard code dataTable
         */
        dataRows.add(0.53243, 1.5325);
        dataRows.add(0.43454, 1.6453);
        dataRows.add(9.23423, 4.2342);
        dataRows.add(8.92344, 4.0234);
        dataRows.add(4.04352, 5.2342);
        dataRows.add(3.92343, 5.3333);

        Network network = new Network();

        Node nodeCluster = new Node("Cluster", new String[] {"Cluster1", "Cluster2", "Cluster3"}); // Node holds 3 clusters: Cluster1, Cluster2, Cluster3
        network.getNodes().add(nodeCluster);

        Variable x = new Variable("X", VariableValueType.CONTINUOUS);
        Variable y = new Variable("Y", VariableValueType.CONTINUOUS);

        Node nodePosition = new Node("Position", x, y); // Holds the position: Variable[x], Variable[y]
        network.getNodes().add(nodePosition);
        network.getLinks().add(new Link(nodeCluster, nodePosition)); //connection between nodeCluster -> nodePosition

        x = network.getVariables().get("X");
        y = network.getVariables().get("Y");

        ParameterLearning parameterLearning = new ParameterLearning(network, new RelevanceTreeInferenceFactory());
        ParameterLearningOptions parameterLearningOptions = new ParameterLearningOptions();

        DataReaderCommand dataReaderCommand = new DataTableDataReaderCommand(dataTable);
        ReaderOptions readerOptions = new ReaderOptions(null);
        VariableReference[] variableReferences = new VariableReference[] {
                new VariableReference(x, ColumnValueType.VALUE, x.getName()),
                new VariableReference(y, ColumnValueType.VALUE, y.getName())
        };
        EvidenceReaderCommand evidenceReaderCommand = new DefaultEvidenceReaderCommand(dataReaderCommand, Arrays.asList(variableReferences), readerOptions);
        ParameterLearningOutput parameterLearningOutput = parameterLearning.learn(evidenceReaderCommand, parameterLearningOptions);
        Main.printer.printConsole(String.format("Log likelihood: %s", parameterLearningOutput.getLogLikelihood()));
        for(int i = 0; i < parameterLearning.getNetwork().getNodes().get("Cluster").getDistribution().getTable().size(); i++) {
            Main.printer.printConsole(String.format("%s", parameterLearning.getNetwork().getNodes().get("Cluster").getDistribution().getTable().get(i)));
        }
    }

    private static void translationTest() throws InterruptedException {
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
