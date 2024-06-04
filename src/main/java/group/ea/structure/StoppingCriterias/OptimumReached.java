package group.ea.structure.StoppingCriterias;

import group.ea.structure.StoppingCriterias.StoppingCriterion;
import group.ea.structure.algorithm.Algorithm;

import java.util.Objects;

public class OptimumReached implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        if(algorithm.getProblem().name.equals("TSP")){
            String name = algorithm.get_sl().get_tsp().getLastPartOfFilename();
            if(Objects.equals(name,"berlin52")){
                return algorithm.getFitness() == 7544;
            }
            if(Objects.equals(name,"a280")){
                return algorithm.getFitness() <= 2581;
            }
            if(Objects.equals(name,"bier127")){
                return algorithm.getFitness() <= 120000;
            }
        }

        return algorithm.getFitness() >= algorithm.getBitStringLength();
    }
}