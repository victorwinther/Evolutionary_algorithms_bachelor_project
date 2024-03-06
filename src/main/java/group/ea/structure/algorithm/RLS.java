package group.ea.structure.algorithm;

import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;

public class RLS extends Algorithm{

    public RLS(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }
    @Override
    public void initialize() {
        BitString.in
    }

    @Override
    public void iterate() {

    }

    @Override
    public boolean stoppingCriteriaMet() {
        return false;
    }

    @Override
    public void runAlgorithm() {
        this.initialize();
        while (!this.stoppingCriteriaMet()) {
            this.iterate();
        }
    }
}
