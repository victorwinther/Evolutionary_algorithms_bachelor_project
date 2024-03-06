package group.ea.structure.problem;

import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;

public abstract class Problem {
    public String name;
    SearchSpace searchSpace;

    public abstract double computeFitness(BitString bitString);

    void init() {}
}


