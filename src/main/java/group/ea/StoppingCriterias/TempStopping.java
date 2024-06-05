package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

public class TempStopping implements StoppingCriterion {
    @Override
    public boolean isMet(Algorithm algorithm) {
        return algorithm.getCurrentTemp() < 1;
    }
}
