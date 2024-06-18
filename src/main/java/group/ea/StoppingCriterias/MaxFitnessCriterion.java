package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

import java.util.Objects;

public class MaxFitnessCriterion implements StoppingCriterion {
    private int maxFitness;

    public MaxFitnessCriterion(int max) {
        this.maxFitness = max;
    }

    @Override
    public boolean isMet(Algorithm algorithm) {
        if (algorithm.getProblem().name.equals("TSP")) {
            if (algorithm.getGeneration() >= 1000000) {
                System.out.println("Optimum not reached for 1000000");
                return true;
            } else {
                return algorithm.getFitness() <= maxFitness;
            }
        }
        return
                algorithm.getFitness() >= maxFitness;
    }
}