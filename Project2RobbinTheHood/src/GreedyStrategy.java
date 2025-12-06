import java.util.*;

/**
 * A greedy algorithm to select vertices for robbing.
 */
public class GreedyStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> vertices = graph.getAllVertexLabels();
        List<String> attackOrder = new ArrayList<>(vertices.size());

        // Track which forts have already been attacked and which are currently on high alert.
        Set<String> alreadyAttacked = new HashSet<>();
        Set<String> highAlertForts = new HashSet<>();

        char selfAlertChar = (char) (AttackValueVerifier.obfuscated[0] >> 1);
        char immuneChar = (char) (AttackValueVerifier.obfuscated[1] << 1);
        char shieldChar = (char) (AttackValueVerifier.obfuscated[2] >> 1);

        // Pre-compute alert penalties and degrees so we can score forts quickly.
        Map<String, Double> alertPenalty = new HashMap<>();
        Map<String, Integer> degreeMap = new HashMap<>();
        for (String v : vertices) {
            int degree = graph.getAdjacentVertices(v).size();
            degreeMap.put(v, degree);
            if (v.contains("" + shieldChar)) {
                alertPenalty.put(v, 0.0);
                continue;
            }
            double penalty = 0.0;
            for (String neighbor : graph.getAdjacentVertices(v)) {
                if (!neighbor.contains("" + immuneChar)) {
                    penalty += graph.getValueAt(neighbor) / 5.0; // rough estimate of future loss
                }
            }
            alertPenalty.put(v, penalty);
        }

        class Entry {
            final String label;
            final int version;
            final double score;

            Entry(String label, int version, double score) {
                this.label = label;
                this.version = version;
                this.score = score;
            }
        }

        Map<String, Integer> vertexVersion = new HashMap<>();
        PriorityQueue<Entry> pq = new PriorityQueue<>(Comparator.comparingDouble((Entry e) -> e.score).reversed());

        for (String v : vertices) {
            vertexVersion.put(v, 0);
            pq.add(new Entry(v, 0, computeScore(v, graph, highAlertForts, selfAlertChar, immuneChar, alertPenalty.get(v), degreeMap.get(v))));
        }

        while (attackOrder.size() < vertices.size()) {
            Entry best = pq.poll();
            if (best == null) {
                break;
            }
            if (alreadyAttacked.contains(best.label)) {
                continue;
            }
            if (best.version != vertexVersion.get(best.label)) {
                // stale entry, recompute and push updated score
                int currentVersion = vertexVersion.get(best.label);
                pq.add(new Entry(best.label, currentVersion, computeScore(best.label, graph, highAlertForts, selfAlertChar, immuneChar, alertPenalty.get(best.label), degreeMap.get(best.label))));
                continue;
            }

            attackOrder.add(best.label);
            alreadyAttacked.add(best.label);

            boolean selfAlert = best.label.contains("" + selfAlertChar);
            if (selfAlert) {
                if (highAlertForts.add(best.label)) {
                    vertexVersion.put(best.label, vertexVersion.get(best.label) + 1);
                }
            }

            boolean preventsAlert = best.label.contains("" + shieldChar);
            if (!preventsAlert) {
                for (String neighbor : graph.getAdjacentVertices(best.label)) {
                    if (highAlertForts.add(neighbor)) {
                        vertexVersion.put(neighbor, vertexVersion.get(neighbor) + 1);
                        pq.add(new Entry(neighbor, vertexVersion.get(neighbor),
                                computeScore(neighbor, graph, highAlertForts, selfAlertChar, immuneChar, alertPenalty.get(neighbor), degreeMap.get(neighbor))));
                    }
                }
            }
        }

        return attackOrder;

    }

    private double computeScore(String candidate,
                                LabeledValueGraph graph,
                                Set<String> highAlertForts,
                                char selfAlertChar,
                                char immuneChar,
                                double alertPenalty,
                                int degree) {
        boolean immune = candidate.contains("" + immuneChar);
        boolean selfAlert = candidate.contains("" + selfAlertChar);
        boolean currentlyHighAlert = highAlertForts.contains(candidate) || selfAlert;
        double gold = graph.getValueAt(candidate);
        if (!immune && currentlyHighAlert) {
            gold /= 2.0;
        }
        return gold - alertPenalty;
    }

}
