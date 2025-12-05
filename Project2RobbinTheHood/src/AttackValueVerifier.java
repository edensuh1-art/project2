import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AttackValueVerifier {

    static final int[] obfuscated = {66, 21, 70, 12, 99};

    /** This function computes the total gold that would be stolen for a given attack ordering.
     * You shouldn't call this method, except for in the BruteForceStrategy algorithm. */
    public static double computeGoldForAttackOrdering(LabeledValueGraph graph, List<String> attackOrdering) {
        return computeGoldForAttackOrdering(graph, attackOrdering, false);
    }

    public static double computeGoldForAttackOrdering(LabeledValueGraph graph, List<String> attackOrdering, boolean printDebugInfo) {
        if (new HashSet<>(attackOrdering).size() != attackOrdering.size()) {
            throw new IllegalArgumentException("Attack ordering contains duplicates: " + attackOrdering);
        }
        double totalGold = 0;
        Set<String> highAlertForts = new HashSet<>();
        for (String fortName : attackOrdering) {
            double goldHere = graph.getValueAt(fortName);
            if (fortName.contains(""+(char)(obfuscated[0]>>1))) {
                highAlertForts.add(fortName);
            }
            if (highAlertForts.contains(fortName) && !fortName.contains(""+(char)(obfuscated[1]<<1))) {
                goldHere = goldHere / 2.0;
            }
            if (printDebugInfo) {
                System.out.println("Stole " + goldHere + " from " + fortName);
            }
            totalGold += goldHere;
            if (!fortName.contains(""+(char)(obfuscated[2]>>1))) {
                List<String> neighbors = graph.adj(fortName);
                highAlertForts.addAll(neighbors);
            }
        }
        return totalGold;
    }
}
