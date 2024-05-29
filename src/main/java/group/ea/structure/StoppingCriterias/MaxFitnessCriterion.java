package group.ea.structure.StoppingCriterias;

import group.ea.structure.StoppingCriterias.StoppingCriterion;
import group.ea.structure.algorithm.Algorithm;

public class MaxFitnessCriterion implements StoppingCriterion {
    private int maxFitness;

    public MaxFitnessCriterion(int max) {
        this.maxFitness = max;
    }

    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getFitness() >= maxFitness;
    }
}