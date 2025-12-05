import java.util.*;

/**
 * An exhaustive search/backtracking strategy to select vertices for robbing.
 */
public class BruteForceStrategy implements RobbingStrategy {

    List<String> bestOrderingFound = new ArrayList<>(); // attack nothing at all?
    // TODO: Add something to keep track of the best value found so far

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
            // TODO: update best value / best ordering if necessary
        } else {
            for (int i = 0; i < remainingVertexLabels.size(); i++) {
                // TODO: make changes to remainingVertexLabels and chosen
                // TODO: do recursive call with updated values
                // TODO: undo changes to remainingVertexLabels and chosen
            }
        }
    }

}
