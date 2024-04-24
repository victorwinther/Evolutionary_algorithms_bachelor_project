package group.ea.structure.algorithm;

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