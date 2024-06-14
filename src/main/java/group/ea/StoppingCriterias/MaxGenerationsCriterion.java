package group.ea.StoppingCriterias;


import group.ea.algorithm.Algorithm;

public class MaxGenerationsCriterion implements StoppingCriterion {
    private int maxGenerations;

    public MaxGenerationsCriterion(int max) {
        this.maxGenerations = max;
    }

    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getGeneration() >= maxGenerations;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }
}
