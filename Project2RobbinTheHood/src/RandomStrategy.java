import java.util.Collections;
import java.util.List;

/**
 * A greedy algorithm to select vertices for robbing.
 */
public class RandomStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> vertices = graph.getAllVertexLabels();
        Collections.shuffle(vertices);
        return vertices;

    }

}
