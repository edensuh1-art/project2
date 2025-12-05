import java.util.Collections;
import java.util.List;

/**
 * A greedy algorithm to select vertices for robbing.
 */
public class GreedyStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> vertices = graph.getAllVertexLabels();
        //TODO: Implement a greedy strategy to select the order of vertices to attack

        return vertices;

    }

}
