import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class creates random acyclic graphs by adding vertices one by one,
 * and each time a new vertex is added, it has some probability of being
 * linked to an existing one by an edge.  (Convince yourself that
 * this process can never result in a cycle, and that it has the potential
 * to generate all possible forest/tree structures -- although *not* with
 * uniform probability!)
 */
public class RandomForestGenerator {
    private Random randGen;

    public RandomForestGenerator(Random randGen) {
        this.randGen = randGen;
    }

    private static String uniqueShortAlphabeticText(int uniqueID) {
        StringBuilder sb = new StringBuilder(7);
        do {
            char ch = (char) ('A' + (uniqueID % 26));
            sb.insert(0,ch);
            uniqueID = uniqueID / 26;
        } while (uniqueID > 0);
        return sb.toString();
    }

    /**
     * @param N                  - number of vertices in the generated graph
     * @param maxGold            - gold for each vertex will be chosen from 1 up to maxGold
     * @param addEdgeProbability - probability of adding an edge whenever a vertex is added.
     *                           - if this probability is 1, then you'll get a single tree
     *                           - instead of a forest.
     * @return a randomly generated acyclic graph
     */
    public LabeledValueGraph makeRandomAcyclicGraph(int N, int maxGold, double addEdgeProbability,
                                                           double specialNoHidingProbability,
                                                           double specialParanoidProbability,
                                                           double specialNoMessengersProbability) {
        LabeledValueGraph graph = new LabeledValueGraph();
        List<String> vertexLabels = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String label = uniqueShortAlphabeticText(i);
            if (randGen.nextDouble() < specialNoHidingProbability) {
                label = label + "*";
            }
            if (randGen.nextDouble() < specialParanoidProbability) {
                label = label + "!";
            }
            if (randGen.nextDouble() < specialNoMessengersProbability) {
                label = label + "#";
            }
            vertexLabels.add(label);
            graph.addVertex(vertexLabels.get(i), randGen.nextInt(1, maxGold + 1));
        }
		// to avoid bias from generational algorithm that earlier vertices in the graph's vertex list order would tend to have more connections
		Collections.shuffle(vertexLabels, randGen);

		// add edges
        for (int i = 1; i < N; i++) {
            if (randGen.nextDouble() < addEdgeProbability) {
                int otherVertex = randGen.nextInt(0, i);
                graph.addEdge(vertexLabels.get(i), vertexLabels.get(otherVertex));
            }
        }
        return graph;
    }

    /**
     * @param N   - number of vertices in the chain
     * @param maxGold - gold for each vertex will be chosen from 1 up to maxGold
     * @return a simple linear (chain) graph with N vertices
     */
    public LabeledValueGraph makeRandomChainGraph(int N, int maxGold) {
        LabeledValueGraph graph = new LabeledValueGraph();
        List<String> vertexLabels = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String label = uniqueShortAlphabeticText(i);
            vertexLabels.add(label);
            graph.addVertex(vertexLabels.get(i), randGen.nextInt(1, maxGold + 1));
        }

        // add edges in chain
        for (int i = 1; i < N; i++) {
            graph.addEdge(vertexLabels.get(i-1), vertexLabels.get(i));
        }
        return graph;
    }

    public static void main(String[] args) throws FileNotFoundException {

        RandomForestGenerator maker = new RandomForestGenerator(new Random());
//        LabeledValueGraph graph = maker.makeRandomAcyclicGraph(1000, 8, 0.95,
//				0.05, 0.05, 0.05);
//        graph.saveToFile("samples/another1000.graph");

        LabeledValueGraph graph = maker.makeRandomAcyclicGraph(8, 9, 1,
				0.00, 0.0, 0.0);
        graph.saveToFile("samples/tree8.graph");



//        RandomForestGenerator maker = new RandomForestGenerator(new Random());
//        LabeledValueGraph chain = maker.makeRandomChainGraph(26, 8);
//        chain.saveToFile("samples/chain26.graph");



    }

}
