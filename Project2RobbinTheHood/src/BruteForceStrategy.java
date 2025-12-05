import java.util.*;

/**
 * An exhaustive search/backtracking strategy to select vertices for robbing.
 */
public class BruteForceStrategy implements RobbingStrategy {

    List<String> bestOrderingFound = new ArrayList<>(); // attack nothing at all?
    double bestValueFound = Double.NEGATIVE_INFINITY;

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> allVertexLabels = graph.getAllVertexLabels();
        List<String> chosen = new ArrayList<>();
        tryAllPermutations(graph, allVertexLabels, chosen);

        return bestOrderingFound;
    }

    private void tryAllPermutations(LabeledValueGraph graph, List<String> remainingVertexLabels, List<String> chosen) {
//        System.out.println("For Debug, Chosen: " + chosen + "  Remaining: " + remainingVertexLabels);
        if (remainingVertexLabels.isEmpty()) {
            double chosenValue = AttackValueVerifier.computeGoldForAttackOrdering(graph, chosen);
            if (chosenValue > bestValueFound) {
                bestValueFound = chosenValue;
                bestOrderingFound = new ArrayList<>(chosen);
            }
        } else {
            for (int i = 0; i < remainingVertexLabels.size(); i++) {
                String chosenLabel = remainingVertexLabels.remove(i);
                chosen.add(chosenLabel);

                tryAllPermutations(graph, remainingVertexLabels, chosen);

                chosen.remove(chosen.size() - 1);
                remainingVertexLabels.add(i, chosenLabel);
            }
        }
    }

}
