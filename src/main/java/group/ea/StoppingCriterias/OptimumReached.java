package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

import java.util.Objects;

public class OptimumReached implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        if (algorithm.getProblem().name.equals("TSP")) {
            if (algorithm.getGeneration() >= 1000000) {
                System.out.println("Optimum reached for 1000000");
                return true;
            }
            String name = algorithm.get_sl().get_tsp().getLastPartOfFilename();
            if (Objects.equals(name, "berlin52")) {
                if (algorithm.getFitness() == 7544) {
                    System.out.println("Optimum reached for berlin52");
                }
                return algorithm.getFitness() == 7544;
            }
            if (Objects.equals(name, "a280")) {
                return algorithm.getFitness() <= 2581;
            }
            if (Objects.equals(name, "bier127")) {
                return algorithm.getFitness() <= 120000;
            }
        }

        return algorithm.getFitness() >= algorithm.getBitStringLength();
    }
}