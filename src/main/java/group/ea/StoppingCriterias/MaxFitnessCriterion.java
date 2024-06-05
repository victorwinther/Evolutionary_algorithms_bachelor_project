package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

public class MaxFitnessCriterion implements StoppingCriterion {
    private int maxFitness;

    public MaxFitnessCriterion(int max) {
        this.maxFitness = max;
    }

    @Override
    public boolean isMet(Algorithm algorithm) {
        if(algorithm.getProblem().name.equals("TSP")){
            return algorithm.getFitness() <= maxFitness;
        }
        return
                algorithm.getFitness() >= maxFitness;
    }
}