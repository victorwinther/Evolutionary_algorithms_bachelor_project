package group.ea.structure.algorithm;

public class OptimumReached implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getFitness() >= algorithm.getBitStringLength();
    }
}