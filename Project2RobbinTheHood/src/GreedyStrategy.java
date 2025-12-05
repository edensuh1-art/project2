import java.util.*;

/**
 * A greedy algorithm to select vertices for robbing.
 */
public class GreedyStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> vertices = graph.getAllVertexLabels();
        List<String> attackOrder = new ArrayList<>();

        // Track which forts have already been attacked and which are currently on high alert.
        Set<String> alreadyAttacked = new HashSet<>();
        Set<String> highAlertForts = new HashSet<>();

        while (attackOrder.size() < vertices.size()) {
            String bestCandidate = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (String candidate : vertices) {
                if (alreadyAttacked.contains(candidate)) {
                    continue;
                }

                double immediateGold = graph.getValueAt(candidate);
                boolean candidateImmuneToHalving = candidate.contains("" + (char) (AttackValueVerifier.obfuscated[1] << 1));
                if (highAlertForts.contains(candidate) && !candidateImmuneToHalving) {
                    immediateGold /= 2.0;
                }

                // Estimate the cost of putting neighbors on high alert: each non-alert neighbor will likely
                // lose half its value when eventually attacked (unless it's immune to halving).
                double estimatedFutureLoss = 0;
                boolean candidatePreventsAlert = candidate.contains("" + (char) (AttackValueVerifier.obfuscated[2] >> 1));
                if (!candidatePreventsAlert) {
                    for (String neighbor : graph.getAdjacentVertices(candidate)) {
                        boolean neighborImmuneToHalving = neighbor.contains("" + (char) (AttackValueVerifier.obfuscated[1] << 1));
                        if (!neighborImmuneToHalving && !highAlertForts.contains(neighbor)) {
                            estimatedFutureLoss += graph.getValueAt(neighbor) / 2.0;
                        }
                    }
                }

                double score = immediateGold - estimatedFutureLoss;
                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }

            attackOrder.add(bestCandidate);
            alreadyAttacked.add(bestCandidate);

            // Update high alert list based on the attack we just chose, mirroring AttackValueVerifier rules.
            if (bestCandidate.contains("" + (char) (AttackValueVerifier.obfuscated[0] >> 1))) {
                highAlertForts.add(bestCandidate);
            }

            boolean candidatePreventsAlert = bestCandidate.contains("" + (char) (AttackValueVerifier.obfuscated[2] >> 1));
            if (!candidatePreventsAlert) {
                highAlertForts.addAll(graph.getAdjacentVertices(bestCandidate));
            }
        }

        return attackOrder;

    }

}
