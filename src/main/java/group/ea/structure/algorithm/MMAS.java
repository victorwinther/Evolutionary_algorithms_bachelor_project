package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

public class MMAS extends ACO {

    private double minPheremone;
    private double maxPheremone;

    public MMAS(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        maxPheremone = 1.0 / ((evaporation * getValue()));
        minPheremone = maxPheremone / (2.0 * dimension);
        Q = maxPheremone;
        System.out.println("Max and min values " + maxPheremone + " " + minPheremone);
        _localSearch = false;
        _IBFlag = false;

    }

    public double getValue(){
        _cloneSl = new Solution(_sl.get_tsp());

        localSearch();
        return _cloneSl.computeFitness();

    }

    @Override
    public void updatePheromone() {
        super.updatePheromone();
        limitPheromones();
    }

    private void limitPheromones(){
        for (int i = 0; i  < (dimension / 2); i++){
            for (int j = 0; j < (dimension / 2); j++){
                if (pheromone[i][j] > maxPheremone) {
                    pheromone[i][j] = maxPheremone;
                    pheromone[j][i] = maxPheremone;
                } else if (pheromone[i][j] < minPheremone) {
                    pheromone[i][j] = minPheremone;
                    pheromone[j][i] = minPheremone;
                }
            }
        }
    }
}
