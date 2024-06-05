package group.ea.StoppingCriterias;

import group.ea.algorithm.Algorithm;

public interface StoppingCriterion {
    boolean isMet(Algorithm algorithm);
}
