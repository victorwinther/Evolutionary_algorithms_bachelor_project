package group.ea.algorithm;



import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class ELITIST extends ACO{


    public ELITIST(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }

    @Override
    public void updatePheromone(){
        updatePheromoneBEST(bestAnt);
        updatePheromoneALL();
    }

    public boolean getLocalSearch() {
        return _localSearch;
    }
    public boolean getIBFlag(){
        return _IBFlag;
    }


}
