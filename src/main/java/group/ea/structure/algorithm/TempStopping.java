package group.ea.structure.algorithm;

public class TempStopping implements StoppingCriterion{
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getCurrentTemp() < 1;
    }
}
