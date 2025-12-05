import java.util.List;

/**
 * A dynamics programming algorithm to select vertices for robbing.
 */
public class DPStrategy implements RobbingStrategy {

    @Override
    public List<String> chooseOrderToAttack(LabeledValueGraph graph) {

        List<String> vertices = graph.getAllVertexLabels();

        //TODO: Implement a DP strategy to select the order of vertices to attack

        return vertices;

    }

}
