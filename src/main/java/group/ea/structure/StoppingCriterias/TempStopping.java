package group.ea.structure.StoppingCriterias;

import group.ea.structure.algorithm.Algorithm;

public class TempStopping implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getCurrentTemp() < 1;
    }
}
