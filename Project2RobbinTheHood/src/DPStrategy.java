import java.util.*;

/**
 * A dynamics programming algorithm to select vertices for robbing.
 */
public class DPStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {
        List<String> attackOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String root : graph.getAllVertexLabels()) {
            if (visited.contains(root)) {
                continue;
            }
            NodeDP dp = dfs(root, null, graph, visited);
            attackOrder.addAll(dp.bestNoAlertOrder);
        }

        return attackOrder;
    }

    private static class NodeDP {
        final String label;
        final boolean hasShield;
        final boolean immune;
        final boolean selfAlert;
        double bestNoAlertValue;
        List<String> bestNoAlertOrder;
        double bestAlertValue;
        List<String> bestAlertOrder;

        NodeDP(String label) {
            this.label = label;
            this.hasShield = label.contains("" + (char) (AttackValueVerifier.obfuscated[2] >> 1));
            this.immune = label.contains("" + (char) (AttackValueVerifier.obfuscated[1] << 1));
            this.selfAlert = label.contains("" + (char) (AttackValueVerifier.obfuscated[0] >> 1));
        }
    }

    private NodeDP dfs(String label, String parent, LabeledValueGraph graph, Set<String> visited) {
        visited.add(label);
        NodeDP current = new NodeDP(label);

        List<NodeDP> children = new ArrayList<>();
        for (String neighbor : graph.getAdjacentVertices(label)) {
            if (neighbor.equals(parent)) {
                continue;
            }
            if (visited.contains(neighbor)) {
                continue; // skip in case the graph has an unexpected cycle
            }
            children.add(dfs(neighbor, label, graph, visited));
        }

        computeBestOrders(current, children, graph);
        return current;
    }

    private void computeBestOrders(NodeDP node, List<NodeDP> children, LabeledValueGraph graph) {
        node.bestNoAlertOrder = computeBestOrderForInitialAlert(node, children, false, graph);
        node.bestNoAlertValue = AttackValueVerifier.computeGoldForAttackOrdering(graph, node.bestNoAlertOrder);

        node.bestAlertOrder = computeBestOrderForInitialAlert(node, children, true, graph);
        node.bestAlertValue = AttackValueVerifier.computeGoldForAttackOrdering(graph, node.bestAlertOrder);
    }

    private List<String> computeBestOrderForInitialAlert(NodeDP node, List<NodeDP> children, boolean initialAlert,
                                                         LabeledValueGraph graph) {
        // Option 1: attack this node first, then all children
        List<String> earlyOrder = new ArrayList<>();
        boolean alertAtNodeEarly = initialAlert || node.selfAlert;
        double nodeGoldEarly = computeGold(node.label, alertAtNodeEarly, node.immune, graph);
        double earlyTotal = nodeGoldEarly;
        earlyOrder.add(node.label);
        for (NodeDP child : children) {
            if (node.hasShield) {
                earlyOrder.addAll(child.bestNoAlertOrder);
                earlyTotal += child.bestNoAlertValue;
            } else {
                earlyOrder.addAll(child.bestAlertOrder);
                earlyTotal += child.bestAlertValue;
            }
        }

        // Option 2: attack all children first, then this node
        List<String> lateOrder = new ArrayList<>();
        double lateTotal = 0.0;
        boolean alertedByChild = false;
        for (NodeDP child : children) {
            lateOrder.addAll(child.bestNoAlertOrder);
            lateTotal += child.bestNoAlertValue;
            if (!child.hasShield) {
                alertedByChild = true;
            }
        }
        boolean alertAtNodeLate = initialAlert || node.selfAlert || alertedByChild;
        double nodeGoldLate = computeGold(node.label, alertAtNodeLate, node.immune, graph);
        lateTotal += nodeGoldLate;
        lateOrder.add(node.label);

        if (lateTotal > earlyTotal) {
            return lateOrder;
        }
        return earlyOrder;
    }

    private double computeGold(String label, boolean alerted, boolean immune, LabeledValueGraph graph) {
        double gold = graph.getValueAt(label);
        if (alerted && !immune) {
            gold /= 2.0;
        }
        return gold;
    }
}
