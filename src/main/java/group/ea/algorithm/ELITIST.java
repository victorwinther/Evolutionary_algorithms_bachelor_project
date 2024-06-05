package group.ea.structure.algorithm;

import group.ea.structure.problem.Problem;
import group.ea.structure.TSP.Solution;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

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


}
