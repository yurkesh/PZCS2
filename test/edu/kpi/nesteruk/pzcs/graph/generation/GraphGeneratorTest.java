package edu.kpi.nesteruk.pzcs.graph.generation;

import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.primitives.HasWeight;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.IntSummaryStatistics;
import java.util.Random;

import static edu.kpi.nesteruk.util.RandomUtils.randomFromTo;
import static org.testng.Assert.*;

/**
 * Created by Anatolii Bed on 2016-04-19.
 */
public class GraphGeneratorTest extends Assert {

    public static final long SEED_FOR_RANDOM = 19;
    public static final int NUMBER_OF_GENERATIONS = 1000;

    public static final Random PARAMS_RANDOM = new Random(42);

    public static final int MIN_NODES_COUNT = 2;
    public static final int MAX_NODES_COUNT = 100;
    public static final double MIN_COHERENCE = 0.1;
    public static final double MAX_COHERENCE = 0.9;
    public static final int MIN_NODE_WEIGHT = 1;
    public static final int MAX_NODE_WEIGHT = 1000;
    public static final int MIN_LINK_WEIGHT = 1;
    public static final int MAX_LINK_WEIGHT = 1000;

    private GraphGenerator<Task, DirectedLink<Task>, TasksGraphBundle> graphGenerator;

    @BeforeTest
    public void setupGenerator() {
        graphGenerator = TasksGraphModel.makeGenerator(new Random(SEED_FOR_RANDOM));
    }

    @Test(testName = "Test with predefined params")
    public void testGenerateWithPredefined() throws Exception {
        testWithParams(getParams());
    }

    @Test(testName = "Test with random params", invocationCount = NUMBER_OF_GENERATIONS)
    public void testGenerateWithRandom() throws Exception {
        testWithParams(generateParams());
    }

    private void testWithParams(Params params) {
        checkParams(params);

        GraphModelBundle<Task, DirectedLink<Task>> modelBundle = graphGenerator.generate(params);

        checkNumberOfNodes(modelBundle, params);
        checkNodesWeight(modelBundle, params);
        checkCoherence(modelBundle, params);
    }

    private static void checkCoherence(GraphModelBundle<Task, DirectedLink<Task>> modelBundle, Params params) {
        double correlation = GraphUtils.getGraphCorrelation(modelBundle.getNodesMap().values(), modelBundle.getLinksMap().values());
        assertEquals(correlation, params.coherence, 0.1, "Correlation does not match");
    }

    private static void checkNodesWeight(GraphModelBundle<Task, DirectedLink<Task>> modelBundle, Params params) {
        IntSummaryStatistics statistics = modelBundle.getNodesMap().values().stream().mapToInt(HasWeight::getWeight).summaryStatistics();
        assertTrue(statistics.getMin() >= params.minNodeWeight, "Min weight of nodes is incorrect");
        assertTrue(statistics.getMax() <= params.maxNodeWeight, "Max weight of nodes is incorrect");
    }

    private static void checkNumberOfNodes(GraphModelBundle<Task, DirectedLink<Task>> modelBundle, Params params) {
        int nodesCount = modelBundle.getNodesMap().values().size();
        assertEquals(nodesCount, params.numberOfNodes, "Number of nodes is not correct");
    }

    private static void checkParams(Params params) {
        Params.CheckResult check = Params.isCorrect(params);
        if(check != Params.CheckResult.OK) {
            throw new IllegalStateException("Params are incorrect = " + check + ". Params = " + params);
        }
    }

    private static Params generateParams() {
        Params.Builder builder = new Params.Builder();

        builder.setNumberOfNodes(randomFromTo(PARAMS_RANDOM, MIN_NODES_COUNT, MAX_NODES_COUNT));

        int minNodeWeight = randomFromTo(PARAMS_RANDOM, MIN_NODE_WEIGHT, MAX_NODE_WEIGHT);
        builder.setMinNodeWeight(minNodeWeight);
        builder.setMaxNodeWeight(randomFromTo(PARAMS_RANDOM, minNodeWeight, MAX_NODE_WEIGHT));

        builder.setCoherence(PARAMS_RANDOM.nextDouble() * (MAX_COHERENCE - MIN_COHERENCE) + MIN_COHERENCE);

        int minLinksWeight = randomFromTo(PARAMS_RANDOM, MIN_LINK_WEIGHT, MAX_LINK_WEIGHT);
        builder.setMinLinkWeight(minLinksWeight);
        builder.setMaxLinkWeight(randomFromTo(PARAMS_RANDOM, minLinksWeight, MAX_LINK_WEIGHT));

        return builder.build();
    }

    private static Params getParams() {
        return new Params(1, 5, 6, 0.1, 1, 3);
    }
}