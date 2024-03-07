package group.ea.structure.algorithm;

import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

public abstract class Algorithm {
    Problem problem;
    SearchSpace searchSpace;

    public Algorithm(SearchSpace searchSpace, Problem problem) {
        this.searchSpace = searchSpace;
        this.problem = problem;
    }

    public abstract void initialize();
    public abstract void iterate();
    public abstract boolean stoppingCriteriaMet();
    public abstract void runAlgorithm();

    public abstract void updateGraphics();

    public abstract void performSingleUpdate();

}


