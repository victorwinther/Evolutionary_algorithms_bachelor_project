package group.ea.structure.StoppingCriterias;

import group.ea.structure.algorithm.Algorithm;

public interface StoppingCriterion {
    boolean isMet(Algorithm algorithm);
}
