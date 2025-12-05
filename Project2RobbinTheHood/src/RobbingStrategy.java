import java.util.List;

public interface RobbingStrategy {

    public List<String> chooseOrderToAttack(LabeledValueGraph graph);
}
