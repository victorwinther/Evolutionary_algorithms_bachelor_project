package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

public class OptimumReached implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getFitness() >= algorithm.getBitStringLength();
    }
}