package group.ea.problem;

import group.ea.searchspace.SearchSpace;

public abstract class Problem {
    public String name;
    SearchSpace searchSpace;


    public abstract double computeFitness(String string);

    void init() {
    }

}


