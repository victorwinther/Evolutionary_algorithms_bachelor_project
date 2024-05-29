package group.ea.structure.StoppingCriterias;

import group.ea.structure.StoppingCriterias.StoppingCriterion;
import group.ea.structure.algorithm.Algorithm;

public class OptimumReached implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getFitness() >= algorithm.getBitStringLength();
    }
}