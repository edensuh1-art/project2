import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {


    public static void main(String args[]) throws FileNotFoundException {
        compareStrategies();
        collectTimingData();
    }

    public static void compareStrategies() throws FileNotFoundException {
        String inputFile = "samples/tree8.graph";
//        String inputFile = "samples/sherwood_forest.graph";
//        String inputFile = "samples/random1000.graph";
        LabeledValueGraph graph = new LabeledValueGraph(inputFile);

        List<RobbingStrategy> strategies = new ArrayList<>();
        strategies.add(new RandomStrategy());
        strategies.add(new BruteForceStrategy());
//        strategies.add(new GreedyStrategy());
//        strategies.add(new DPStrategy());

        for (RobbingStrategy strategy : strategies) {
            testStrategy(strategy, graph);
        }
    }

    public static void testStrategy(RobbingStrategy strategy, LabeledValueGraph originalGraph) {
        //make a copy to pass to the strategy, in case the strategy modifies the graph in some way
        LabeledValueGraph copy = new LabeledValueGraph(originalGraph);
        List<String> fortAttackOrdering = strategy.chooseOrderToAttack(copy);

        double gold = AttackValueVerifier.computeGoldForAttackOrdering(originalGraph, fortAttackOrdering, false);
        System.out.println(gold + " stolen by " + strategy.getClass().getSimpleName());
        System.out.println("   using order: " + fortAttackOrdering);
    }

    public static void collectTimingData() throws FileNotFoundException {
        RandomForestGenerator maker = new RandomForestGenerator(new Random());

        System.out.println("       N  Time(s)");
        RobbingStrategy strategy = null;

        for (int N = 1000; N <= 1024000; N = N * 2) {
            LabeledValueGraph graph = maker.makeRandomAcyclicGraph(N, 10, 0.99,
                    0.2, 0.2, 0.2);

            //TODO: change this to the appropriate strategy you want to collect timing data for!
            strategy = new RandomStrategy();
            //RobbingStrategy strategy = new BruteForceStrategy();
            //RobbingStrategy strategy = new GreedyStrategy();
            //RobbingStrategy strategy = new DPStrategy();

            double timeSum  = 0.0;
            int NUM_TRIALS = 1;
            for (int trial = 0; trial < NUM_TRIALS; trial++) {
                long startTime = System.currentTimeMillis();
                strategy.chooseOrderToAttack(graph);
                long endTime = System.currentTimeMillis();
                double elapsedTime = (endTime - startTime) / 1000.0;
                timeSum += elapsedTime;
            }
            double avgTime = timeSum / NUM_TRIALS;
            System.out.printf("%8d %5.2f\n", N, avgTime);
        }
        System.out.println("Finished collecting timing data for " + strategy.getClass().getSimpleName());

    }

}
